package com.app.chatApp.handlers;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.app.chatApp.dto.TransientMessageDto;
import com.app.chatApp.repository.HomeMessageListRepo;
import com.app.chatApp.repository.MessagesRepo;
import com.app.chatApp.vo.HomeMessageList;
import com.app.chatApp.vo.Messages;
import com.app.chatApp.vo.enums.MessageStatus;

@Component
public class UtilityHandler {

    private HomeMessageListRepo homeMessageListRepo;
    private MessagesRepo messagesRepo;

    UtilityHandler(HomeMessageListRepo homeMessageListRepo, MessagesRepo messagesRepo) {
        this.homeMessageListRepo = homeMessageListRepo;
        this.messagesRepo = messagesRepo;
    }

    public void saveMessage(TransientMessageDto msg, MessageStatus status) {

        Messages message = new Messages();
        message.setSender(msg.getSender());
        message.setReceiver(msg.getReceiver());
        message.setMsg(msg.getMessage());
        message.setStatus(status);
        message.setSentTime(LocalDateTime.now());

        if (status == MessageStatus.DELIVERED) {
            message.setDelieverdTime(LocalDateTime.now());
        }
        messagesRepo.save(message);
    }

    public void updateHomeMessageList(TransientMessageDto msg, MessageStatus status) {

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
        home.setStatus(status);
        home.setLastMessageTime(LocalDateTime.now());

        homeMessageListRepo.save(home);
    }

    @org.springframework.transaction.annotation.Transactional
    public java.util.List<String> deliverPendingMessages(String receiverMblNo) {
        java.util.List<String> uniqueSenders = messagesRepo.findUniqueSendersWithSentMessages(receiverMblNo);
        if (!uniqueSenders.isEmpty()) {
            messagesRepo.updateStatusByReceiver(receiverMblNo, MessageStatus.SENT, MessageStatus.DELIVERED, LocalDateTime.now());
            homeMessageListRepo.updateStatusByReceiver(receiverMblNo, MessageStatus.SENT, MessageStatus.DELIVERED);
        }
        return uniqueSenders;
    }
}
