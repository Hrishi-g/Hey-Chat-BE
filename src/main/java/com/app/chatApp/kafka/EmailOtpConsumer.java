package com.app.chatApp.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
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
        topicPartitions = @TopicPartition(topic = "email-otp", partitions = { "0" }),
        groupId = "email-otp-group",
        containerFactory = "emailOtpListenerContainerFactory"
    )
    public void consumeLoginOtp(EmailOtpPayload payload) {
        System.out.println("Kafka Consumer: Received Login OTP request: " + payload);
        emailService.sendLoginOTP(payload.getEmail(), payload.getOtp(), payload.getName());
    }

    @KafkaListener(
        topicPartitions = @TopicPartition(topic = "email-otp", partitions = { "1" }),
        groupId = "email-otp-group",
        containerFactory = "emailOtpListenerContainerFactory"
    )
    public void consumeSignupOtp(EmailOtpPayload payload) {
        System.out.println("Kafka Consumer: Received Signup OTP request: " + payload);
        emailService.sendSignupOTP(payload.getEmail(), payload.getOtp(), payload.getName());
    }
}
