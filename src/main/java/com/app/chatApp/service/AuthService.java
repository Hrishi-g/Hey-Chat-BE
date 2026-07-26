package com.app.chatApp.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.app.chatApp.repository.RegisteredUsersRepo;
import com.app.chatApp.security.JwtUtil;
import com.app.chatApp.vo.RegisteredUsers;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {

    private RegisteredUsersRepo userRepo;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private CookieUtil cookieUtil;

    public AuthService(RegisteredUsersRepo userRepo, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtUtil jwtUtil, CookieUtil cookieUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
    }

    public ResponseEntity<String> signUp(SignupDto userDto) {
        String user = userRepo.findByMblNo(userDto.getMblNo()).orElse(null);
        if (user != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }

        if (!userDto.getPass().equals(userDto.getConfirmPass())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password does not match");
        }
        RegisteredUsers newUser = new RegisteredUsers();
        newUser.setMblNo(userDto.getMblNo());
        newUser.setName(userDto.getName());
        newUser.setPass(passwordEncoder.encode(userDto.getPass()));
        newUser.setDob(userDto.getDob());
        newUser.setGender(userDto.getGender());
        newUser.setImgUrl(userDto.getImgUrl());

        userRepo.save(newUser);
        return ResponseEntity.ok("User Registered Successfully");
    }

    public ResponseEntity<String> login(LoginDto userDto, String clientType, HttpServletResponse httpResponse) {
        try {
            Authentication auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(userDto.getMblNo(), userDto.getPass()));

            RegisteredUsers user = (RegisteredUsers) auth.getPrincipal();
            String jwtToken = jwtUtil.generateJwtToken(user);

            if (clientType.equals("web")) {
                cookieUtil.addJwtCookie(httpResponse, jwtToken);
                return ResponseEntity.ok("Login Success");
            } else {
                return ResponseEntity.ok("Token: " + jwtToken);
            }
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Credentials");
        }
    }

    public ResponseEntity<String> logout(HttpServletResponse httpResponse) {
        cookieUtil.clearJwtCookie(httpResponse);
        return ResponseEntity.ok("Logout Success");
    }
}
