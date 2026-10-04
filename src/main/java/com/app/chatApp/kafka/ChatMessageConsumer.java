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

import com.app.chatApp.handlers.UtilityHandler;

@Service
@Transactional
public class ChatMessageConsumer {

    private final MessagesRepo messagesRepo;
    private final HomeMessageListRepo homeMessageListRepo;
    private final UtilityHandler utilityHandler;

    ChatMessageConsumer(MessagesRepo messagesRepo, HomeMessageListRepo homeMessageListRepo, UtilityHandler utilityHandler) {
        this.messagesRepo = messagesRepo;
        this.homeMessageListRepo = homeMessageListRepo;
        this.utilityHandler = utilityHandler;
    }

    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void consumeChatMessage(TransientMessageDto msg) {
        if (msg == null) {
            return;
        }

        String type = msg.getType();
        if ("EDIT".equals(type)) {
            utilityHandler.processEditInDatabase(msg);
        } else if ("DELETE_FOR_ME".equals(type) || "DELETE_EVERYONE".equals(type)) {
            utilityHandler.processDeleteInDatabase(msg);
        } else {
            // Standard CHAT message: save to Messages & update HomeMessageList
            saveChatMessageAndHome(msg);
        }
    }

    private void saveChatMessageAndHome(TransientMessageDto msg) {
        Messages message = new Messages();
        message.setClientMsgId(msg.getMsgId());
        message.setSender(msg.getSender());
        message.setReceiver(msg.getReceiver());
        message.setMsg(msg.getMessage());
        message.setStatus(msg.getStatus());

        LocalDateTime sentTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(msg.getTimeStamp() != 0 ? msg.getTimeStamp() : System.currentTimeMillis()),
                ZoneId.systemDefault());
        message.setSentTime(sentTime);
        if (msg.getStatus() != null && msg.getStatus().equals(MessageStatus.DELIVERED)) {
            message.setDelieverdTime(LocalDateTime.now());
        }
        messagesRepo.save(message);

        // Update home message summary
        Optional<HomeMessageList> existingChat = homeMessageListRepo.checkIfUserExistInHomeMessageChat(
                msg.getSender(),
                msg.getReceiver());

        HomeMessageList home = existingChat.orElseGet(() -> {
            HomeMessageList h = new HomeMessageList();
            h.setSender(msg.getSender());
            h.setReceiver(msg.getReceiver());
            return h;
        });

        home.setLastMsg(msg.getMessage());
        home.setStatus(msg.getStatus());
        home.setLastMessageTime(sentTime);

        homeMessageListRepo.save(home);
    }
}
