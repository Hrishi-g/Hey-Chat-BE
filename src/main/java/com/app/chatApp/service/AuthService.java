package com.app.chatApp.service;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.app.chatApp.config.CookieUtil;
import com.app.chatApp.dto.LoginDto;
import com.app.chatApp.dto.SignupDto;
import com.app.chatApp.dto.SignupSessionDto;
import com.app.chatApp.dto.EmailOtpPayload;
import com.app.chatApp.repository.RegisteredUsersRepo;
import com.app.chatApp.security.JwtUtil;
import com.app.chatApp.vo.RegisteredUsers;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {

    private RegisteredUsersRepo userRepo;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private CookieUtil cookieUtil;
    private RedisTemplate<String, Object> redisTemplate;
    private KafkaTemplate<String, Object> kafkaTemplate;

    public AuthService(RegisteredUsersRepo userRepo, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtUtil jwtUtil, CookieUtil cookieUtil, 
        RedisTemplate<String, Object> redisTemplate, KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    public ResponseEntity<String> signUp(SignupDto userDto) {
        if (userRepo.existsByMblNo(userDto.getMblNo()) || userRepo.findByMblNo(userDto.getMblNo()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User mobile number already registered");
        }
        if (userDto.getEmail() != null && userRepo.existsByEmail(userDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User email already registered");
        }

        if (!userDto.getPass().equals(userDto.getConfirmPass())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password does not match");
        }

        // Check if signup OTP is already active in Redis
        Object storedSessionObj = redisTemplate.opsForValue().get("signup_session:" + userDto.getMblNo());
        if (storedSessionObj != null) {
            SignupSessionDto session;
            if (storedSessionObj instanceof SignupSessionDto) {
                session = (SignupSessionDto) storedSessionObj;
            } else {
                ObjectMapper mapper = new ObjectMapper();
                session = mapper.convertValue(storedSessionObj, SignupSessionDto.class);
            }
            if (session.getSignupData() != null && session.getSignupData().getEmail().equals(userDto.getEmail())) {
                return ResponseEntity.ok("OTP_ACTIVE:OTP already sent on email");
            }
        }

        // Generate new OTP
        Random random = new Random();
        int otp = random.nextInt(900000) + 100000;
        String otpStr = String.valueOf(otp);

        // Store OTP and signup data together in single Redis key for 5 minutes
        SignupSessionDto signupSession = new SignupSessionDto(otpStr, userDto);
        redisTemplate.opsForValue().set("signup_session:" + userDto.getMblNo(), signupSession, Duration.ofMinutes(5));

        // Send OTP via Kafka (Partition 1: Signup)
        EmailOtpPayload payload = new EmailOtpPayload(userDto.getEmail(), otpStr, userDto.getName(), "signup");
        kafkaTemplate.send("email-otp", 1, userDto.getMblNo(), payload);

        // System.out.println("Signup OTP: " + otpStr);
        return ResponseEntity.ok("OTP sent successfully, Email: " + userDto.getEmail());
    }

    public ResponseEntity<String> verifySignup(String mblNo, String otp) {
        Object storedSessionObj = redisTemplate.opsForValue().get("signup_session:" + mblNo);
        if (storedSessionObj == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired OTP");
        }

        SignupSessionDto session;
        if (storedSessionObj instanceof SignupSessionDto) {
            session = (SignupSessionDto) storedSessionObj;
        } else {
            ObjectMapper mapper = new ObjectMapper();
            session = mapper.convertValue(storedSessionObj, SignupSessionDto.class);
        }

        if (session.getOtp() == null || !session.getOtp().equals(otp)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired OTP");
        }

        SignupDto signupDto = session.getSignupData();
        if (signupDto == null) {
            throw new ResponseStatusException(HttpStatus.GONE, "Signup session expired");
        }

        if (signupDto.getEmail() != null && userRepo.existsByEmail(signupDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User email already registered");
        }

        // Save new user
        RegisteredUsers newUser = new RegisteredUsers();
        newUser.setMblNo(signupDto.getMblNo());
        newUser.setName(signupDto.getName());
        newUser.setPass(passwordEncoder.encode(signupDto.getPass()));
        newUser.setDob(signupDto.getDob());
        newUser.setGender(signupDto.getGender());
        newUser.setImgUrl(signupDto.getImgUrl());
        newUser.setEmail(signupDto.getEmail());

        try {
            userRepo.save(newUser);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("email")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User email already registered");
            } else if (msg.contains("mbl") || msg.contains("mobile")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User mobile number already registered");
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User email already registered");
        }

        // Delete single key from Redis
        redisTemplate.delete("signup_session:" + mblNo);

        return ResponseEntity.ok("User Registered Successfully");
    }

    public ResponseEntity<String> login(LoginDto userDto, String clientType, HttpServletResponse httpResponse) {
        try {
            Authentication auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(userDto.getMblNo(), userDto.getPass()));

            RegisteredUsers user = (RegisteredUsers) auth.getPrincipal();
            // System.out.println("User Details: " + user.toString());

            String otpResult = setOtp(user.getMblNo(), user.getEmail(), user.getName());
            if (otpResult.startsWith("OTP_ACTIVE:")) {
                return ResponseEntity.ok(otpResult);
            }
            // System.out.println("OTP: " + otpResult);

            return ResponseEntity.ok("OTP sent successfully, Email: " + user.getEmail());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Credentials");
        }
    }

    public ResponseEntity<String> logout(HttpServletResponse httpResponse) {
        cookieUtil.clearJwtCookie(httpResponse);
        return ResponseEntity.ok("Logout Success");
    }

    public String setOtp(String mblNo, String email, String name) {
        String storedOtp = (String) redisTemplate.opsForValue().get("otp:" + mblNo);
        if (storedOtp != null) {
            Long ttl = redisTemplate.getExpire("otp:" + mblNo, TimeUnit.SECONDS);
            if (ttl != null && ttl > 0) {
                return "OTP_ACTIVE:" + ttl;
            }
        }
        Random random = new Random();
        int otp = random.nextInt(900000) + 100000; // Generates a secure 6-digit OTP (100000 - 999999)
        String otpStr = String.valueOf(otp);
        redisTemplate.opsForValue().set("otp:" + mblNo, otpStr, Duration.ofMinutes(5));

        // Send OTP via Kafka (Partition 0: Login)
        EmailOtpPayload payload = new EmailOtpPayload(email, otpStr, name, "login");
        kafkaTemplate.send("email-otp", 0, mblNo, payload);

        return otpStr;
    }

    public boolean verifyOtp(String mblNo, String otp) {
        String storedOtp = (String) redisTemplate.opsForValue().get("otp:" + mblNo);
        if (storedOtp == null) {
            return false;
        }
        if (!storedOtp.equals(otp)) {
            return false;
        }

        redisTemplate.delete("otp:" + mblNo);
        return true;
    }

    public ResponseEntity<String> verifyOtpAndLogin(String mblNo, String otp, String clientType,
            HttpServletResponse httpResponse) {
        // System.out.println("httpResponse: " + httpResponse);
        boolean verified = verifyOtp(mblNo, otp);
        if (!verified) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired OTP");
        }

        RegisteredUsers user = userRepo.findUserByMblNo(mblNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String jwtToken = jwtUtil.generateJwtToken(user);

        if ("web".equalsIgnoreCase(clientType)) {
            cookieUtil.addJwtCookie(httpResponse, jwtToken);
            return ResponseEntity.ok("Login Success");
        } else {
            return ResponseEntity.ok("Token: " + jwtToken);
        }
    }
}
