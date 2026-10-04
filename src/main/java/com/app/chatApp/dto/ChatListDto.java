package com.app.chatApp.dto;

import java.time.LocalDateTime;

public class ChatListDto {

    private String chatUser;
    private String lastMessage;
    private LocalDateTime lastMessageTime;

    public ChatListDto(String chatUser,
            String lastMessage,
            LocalDateTime lastMessageTime) {
        this.chatUser = chatUser;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public String getChatUser() {
        return chatUser;
    }

    public void setChatUser(String chatUser) {
        this.chatUser = chatUser;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

}
