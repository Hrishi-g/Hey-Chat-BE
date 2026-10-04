package com.app.chatApp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieUtil {

    @Value("${cookie.secure}")
    private boolean cookieSecure;

    public void addJwtCookie(HttpServletResponse response, String jwtToken) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from("jwt", jwtToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .sameSite(cookieSecure ? "None" : "Lax")
                .maxAge(3600);
        if (cookieSecure) {
            builder.partitioned(true);
        }
        response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
    }

    public void clearJwtCookie(HttpServletResponse response) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .sameSite(cookieSecure ? "None" : "Lax")
                .maxAge(0);
        if (cookieSecure) {
            builder.partitioned(true);
        }
        response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
    }
}
