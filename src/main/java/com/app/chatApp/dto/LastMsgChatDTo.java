package com.app.chatApp.dto;

import java.time.LocalDateTime;

import com.app.chatApp.vo.enums.MessageStatus;

public class LastMsgChatDTo {
    String chatUser;
    String lastMsg;
    MessageStatus status;
    LocalDateTime lastMessageTime;
    String chatUserName;
    String imgUrl;

    public LastMsgChatDTo(String chatUser, String lastMsg, MessageStatus status,
            LocalDateTime lastMessageTime) {
        this.chatUser = chatUser;
        this.lastMsg = lastMsg;
        this.status = status;
        this.lastMessageTime = lastMessageTime;
    }

    public String getChatUser() {
        return chatUser;
    }

    public void setChatUser(String chatUser) {
        this.chatUser = chatUser;
    }

    public String getChatUserName() {
        return chatUserName;
    }

    public void setChatUserName(String chatUserName) {
        this.chatUserName = chatUserName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

}
