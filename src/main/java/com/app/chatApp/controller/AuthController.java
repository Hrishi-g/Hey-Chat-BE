package com.app.chatApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.chatApp.dto.LoginDto;
import com.app.chatApp.dto.SignupDto;
import com.app.chatApp.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService userSrc;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignupDto userDto) {
        return userSrc.signUp(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestHeader(value = "X-Client-Type", required = false, defaultValue = "web") String clientType,
            @RequestBody LoginDto userDto, HttpServletResponse httpResponse) {
        System.out.println("X-Client-Type :" + clientType);
        return userSrc.login(userDto, clientType, httpResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse httpResponse) {
        return userSrc.logout(httpResponse);
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }
}
