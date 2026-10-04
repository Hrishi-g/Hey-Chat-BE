package com.app.chatApp.handlers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.kafka.core.KafkaTemplate;
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
    private KafkaTemplate<String, Object> kafkaTemplate;

    public UtilityHandler(HomeMessageListRepo homeMessageListRepo, MessagesRepo messagesRepo,
            KafkaTemplate<String, Object> kafkaTemplate ) {
        this.homeMessageListRepo = homeMessageListRepo;
        this.messagesRepo = messagesRepo;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void saveMessage(TransientMessageDto msg) {
        String key = getChatKey(msg.getSender(), msg.getReceiver());
        sendWithLogging("chat-messages", key, msg);
    }

    public void sendEditMessage(TransientMessageDto msg) {
        String key = getChatKey(msg.getSender(), msg.getReceiver());
        sendWithLogging("chat-messages", key, msg);
    }

    public void sendDeleteMessage(TransientMessageDto msg) {
        String key = getChatKey(msg.getSender(), msg.getReceiver());
        sendWithLogging("chat-messages", key, msg);
    }

    public void updateHomeMessageList(TransientMessageDto msg) {
        // Handled in single chat-messages consumer when msg is saved
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

    public void markMessagesAsRead(String sender, String receiver) {
       
        messagesRepo.updateStatusBySenderAndReceiver(sender, receiver, MessageStatus.READ, LocalDateTime.now());
        homeMessageListRepo.updateStatusBySenderAndReceiver(sender, receiver, MessageStatus.READ);
       
    }

    public void processEditInDatabase(TransientMessageDto msg) {
       
        if (msg == null || msg.getSender() == null || msg.getMessage() == null || msg.getMessage().trim().isEmpty()) {
            return;
        }

        Optional<Messages> msgOpt = Optional.empty();
        if (msg.getMsgId() != null && !msg.getMsgId().trim().isEmpty()) {
            msgOpt = messagesRepo.findByClientMsgIdAndSender(msg.getMsgId(), msg.getSender());
        }
        if (msgOpt.isEmpty()) {
            List<Messages> latestMsgs = messagesRepo.findLatestMessageBetweenUsers(msg.getSender(), msg.getReceiver());
            msgOpt = latestMsgs.stream().filter(m -> m.getSender().equals(msg.getSender())).findFirst();
        }

        if (msgOpt.isEmpty()) {
            // e.printStackTrace();
            // System.err.println("Async Edit failed: Message not found for clientMsgId=" + msg.getMsgId() + ", sender=" + msg.getSender());
            return;
        }

        Messages message = msgOpt.get();

        if (message.getReceiverTime() != null) {
            LocalDateTime referenceTime = message.getSentTime() != null ? message.getSentTime() : message.getCreatedTime();
            if (referenceTime != null && Duration.between(referenceTime, LocalDateTime.now()).toMinutes() > 10) {
                // System.err.println("Async Edit rejected: Message read > 10 mins ago for clientMsgId=" + msg.getMsgId());
                return;
            }
        }

        message.setMsg(msg.getMessage());
        message.setIsEdited(true);
        message.setEditedTime(LocalDateTime.now());
        messagesRepo.save(message);

        List<Messages> latestMsgs = messagesRepo.findLatestMessageBetweenUsers(message.getSender(), message.getReceiver());
        if (!latestMsgs.isEmpty() && (latestMsgs.get(0).getMsgId().equals(message.getMsgId()) || message.getClientMsgId().equals(msg.getMsgId()))) {
            Optional<HomeMessageList> homeOpt = homeMessageListRepo.checkIfUserExistInHomeMessageChat(message.getSender(), message.getReceiver());
            if (homeOpt.isPresent()) {
                HomeMessageList home = homeOpt.get();
                home.setLastMsg(msg.getMessage());
                homeMessageListRepo.save(home);
            }}
    }

    public void processDeleteInDatabase(TransientMessageDto msg) {
      
        if (msg == null || msg.getSender() == null || msg.getMsgId() == null) {
            return;
        }

        Optional<Messages> msgOpt = messagesRepo.findByClientMsgId(msg.getMsgId());
        if (msgOpt.isEmpty()) {
            return;
        }

        Messages message = msgOpt.get();

        if ("DELETE_EVERYONE".equals(msg.getType())) {
            message.setIsDeletedForEveryone(true);
            message.setDeletedTime(LocalDateTime.now());
            messagesRepo.save(message);

            List<Messages> latestMsgs = messagesRepo.findLatestMessageBetweenUsers(message.getSender(), message.getReceiver());
            if (!latestMsgs.isEmpty() && (latestMsgs.get(0).getMsgId().equals(message.getMsgId()) || message.getClientMsgId().equals(msg.getMsgId()))) {
                Optional<HomeMessageList> homeOpt = homeMessageListRepo.checkIfUserExistInHomeMessageChat(message.getSender(), message.getReceiver());
                if (homeOpt.isPresent()) {
                    HomeMessageList home = homeOpt.get();
                    home.setLastMsg("This message was deleted");
                    homeMessageListRepo.save(home);
                }
            }
        } else if ("DELETE_FOR_ME".equals(msg.getType())) {
            String currentDeletedUsers = message.getDeletedForUsers();
            if (currentDeletedUsers == null || currentDeletedUsers.isEmpty()) {
                message.setDeletedForUsers(msg.getSender());
            } else if (!currentDeletedUsers.contains(msg.getSender())) {
                message.setDeletedForUsers(currentDeletedUsers + "," + msg.getSender());
            }
    }
}
    }
