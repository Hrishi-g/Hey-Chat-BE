package com.app.chatApp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${spring.mail.from}")
    private String mailFrom;

    private JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    // @RateLimiter(name = "emailResetLimiter", fallbackMethod =
    // "sendResetMailFallback")
    public void sendLoginOTP(String toEmail, String otp, String name) {
        System.out.println("Sending password reset email to: " + toEmail);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailFrom);
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("lets-sampark login OTP");
        mailMessage.setText("Hello " + name + ",\n\nOTP for Login: " + otp
                + "\n\nThis OTP will expire in 5 minutes.\n\ns Thank you for using lets-sampark!");
        javaMailSender.send(mailMessage);
        System.out.println("Successfully sent password reset email to: " + toEmail);
    }

    public void sendSignupOTP(String toEmail, String otp, String name) {
        System.out.println("Sending signup verification email to: " + toEmail);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailFrom);
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("lets-sampark signup OTP");
        mailMessage.setText("Hello " + name + ",\n\nOTP for Signup: " + otp
                + "\n\nThis OTP will expire in 5 minutes.\n\nThank you for choosing lets-sampark!");
        javaMailSender.send(mailMessage);
        System.out.println("Successfully sent signup verification email to: " + toEmail);
    }

    // public void sendResetMailFallback(String to, String token, Throwable t) {
    // log.warn("sendResetMail rate-limited for email: {}. Reason: {}", to,
    // t.getMessage());
    // throw new IllegalStateException("You are requesting password reset emails too
    // frequently. Please wait before trying again.");
    // }
}
