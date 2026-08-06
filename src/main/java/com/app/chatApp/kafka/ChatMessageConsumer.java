package com.app.chatApp.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.chatApp.dto.TransientMessageDto;
import com.app.chatApp.repository.HomeMessageListRepo;
import com.app.chatApp.repository.MessagesRepo;
import com.app.chatApp.vo.HomeMessageList;
import com.app.chatApp.vo.Messages;
import com.app.chatApp.vo.enums.MessageStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@Transactional
public class ChatMessageConsumer {

    private MessagesRepo messagesRepo;
    private HomeMessageListRepo homeMessageListRepo;

    ChatMessageConsumer(MessagesRepo messagesRepo, HomeMessageListRepo homeMessageListRepo) {
        this.messagesRepo = messagesRepo;
        this.homeMessageListRepo = homeMessageListRepo;
    }

    @KafkaListener(topics = "chat-messages", groupId = "chat-group", concurrency = "3")
    public void consumeAndSave(TransientMessageDto msg) {
        Messages message = new Messages();
        message.setSender(msg.getSender());
        message.setReceiver(msg.getReceiver());
        message.setMsg(msg.getMessage());
        message.setStatus(msg.getStatus());

        LocalDateTime sentTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(msg.getTimeStamp()),
                ZoneId.systemDefault());
        message.setSentTime(sentTime);
        if (msg.getStatus().equals(MessageStatus.DELIVERED)) {
            message.setDelieverdTime(LocalDateTime.now());
        }
        messagesRepo.save(message);
    }

    @KafkaListener(topics = "home-chat", groupId = "chat-group", concurrency = "3")
    public void consumeAndSaveHome(TransientMessageDto msg) {
        Optional<HomeMessageList> existingChat = homeMessageListRepo.checkIfUserExistInHomeMessageChat(
                msg.getSender(),
                msg.getReceiver());

        HomeMessageList home;

        if (existingChat.isPresent()) {
            home = existingChat.get();
        } else {
            home = new HomeMessageList();
            home.setSender(msg.getSender());
            home.setReceiver(msg.getReceiver());
        }
        home.setLastMsg(msg.getMessage());
        home.setStatus(msg.getStatus());

        LocalDateTime sentTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(msg.getTimeStamp()),
                ZoneId.systemDefault());
        home.setLastMessageTime(sentTime);

        homeMessageListRepo.save(home);

    }
}
