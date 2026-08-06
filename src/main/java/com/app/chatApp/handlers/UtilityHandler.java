package com.app.chatApp.handlers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.app.chatApp.dto.TransientMessageDto;
import com.app.chatApp.repository.HomeMessageListRepo;
import com.app.chatApp.repository.MessagesRepo;
import com.app.chatApp.vo.enums.MessageStatus;

import jakarta.transaction.Transactional;

@Component
public class UtilityHandler {

    private HomeMessageListRepo homeMessageListRepo;
    private MessagesRepo messagesRepo;
    private KafkaTemplate<String, Object> kafkaTemplate;

    UtilityHandler(HomeMessageListRepo homeMessageListRepo, MessagesRepo messagesRepo,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.homeMessageListRepo = homeMessageListRepo;
        this.messagesRepo = messagesRepo;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void saveMessage(TransientMessageDto msg) {
        String key = getChatKey(msg.getSender(), msg.getReceiver());
        sendWithLogging("chat-messages", key, msg);
    }

    public void updateHomeMessageList(TransientMessageDto msg) {
        String key = getChatKey(msg.getSender(), msg.getReceiver());
        sendWithLogging("home-chat", key, msg);
    }

    private String getChatKey(String sender, String receiver) {
        if (sender == null || receiver == null) {
            return "";
        }
        return sender.compareTo(receiver) < 0
                ? sender + "_" + receiver
                : receiver + "_" + sender;
    }

    private void sendWithLogging(String topic, String key, TransientMessageDto msg) {
        kafkaTemplate.send(topic, key, msg).whenComplete((result, ex) -> {
            if (ex != null) {
                System.err.println("Failed to send message to Kafka topic [" + topic + "] with key [" + key + "]: "
                        + ex.getMessage());
            }
        });
    }

    @Transactional
    public List<String> deliverPendingMessages(String receiverMblNo) {
        List<String> uniqueSenders = messagesRepo.findUniqueSendersWithSentMessages(receiverMblNo);
        if (!uniqueSenders.isEmpty()) {
            messagesRepo.updateStatusByReceiver(receiverMblNo, MessageStatus.SENT, MessageStatus.DELIVERED,
                    LocalDateTime.now());
            homeMessageListRepo.updateStatusByReceiver(receiverMblNo, MessageStatus.SENT, MessageStatus.DELIVERED);
        }
        return uniqueSenders;
    }

    @Transactional
    public void markMessagesAsRead(String sender, String receiver) {
        messagesRepo.updateStatusBySenderAndReceiver(sender, receiver, MessageStatus.READ, LocalDateTime.now());
        homeMessageListRepo.updateStatusBySenderAndReceiver(sender, receiver, MessageStatus.READ);
    }
}
