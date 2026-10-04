package com.app.chatApp.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.app.chatApp.dto.EmailOtpPayload;
import com.app.chatApp.service.EmailService;

@Component
public class EmailOtpConsumer {

    private final EmailService emailService;

    public EmailOtpConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(
        topics = "email-otp",
        groupId = "email-otp-group",
        containerFactory = "emailOtpListenerContainerFactory"
    )
    public void consumeOtp(EmailOtpPayload payload) {
        if (payload == null) return;
        // System.out.println("Kafka Consumer: Received OTP request: " + payload);
        if ("signup".equalsIgnoreCase(payload.getType())) {
            emailService.sendSignupOTP(payload.getEmail(), payload.getOtp(), payload.getName());
        } else {
            emailService.sendLoginOTP(payload.getEmail(), payload.getOtp(), payload.getName());
        }
    }
}