package com.app.chatApp.dto;

import java.time.LocalDateTime;

import com.app.chatApp.vo.enums.MessageStatus;

public class ChatDto {
    String msgId;
    String sender;
    String receiver;
    String msg;
    MessageStatus status;
    LocalDateTime sentTime;
    LocalDateTime delieverdTime;
    LocalDateTime receiverTime;
    Boolean isEdited = false;
    Boolean isDeletedForEveryone = false;
    String deletedForUsers;

    public ChatDto(String msgId, String sender, String receiver, String msg, MessageStatus status, LocalDateTime sentTime,
            LocalDateTime delieverdTime, LocalDateTime receiverTime, Boolean isEdited, Boolean isDeletedForEveryone, String deletedForUsers) {
        this.msgId = msgId;
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.status = status;
        this.sentTime = sentTime;
        this.delieverdTime = delieverdTime;
        this.receiverTime = receiverTime;
        this.isEdited = isEdited != null ? isEdited : false;
        this.isDeletedForEveryone = isDeletedForEveryone != null ? isDeletedForEveryone : false;
        this.deletedForUsers = deletedForUsers;
    }

    public ChatDto(String msgId, String sender, String receiver, String msg, MessageStatus status, LocalDateTime sentTime,
            LocalDateTime delieverdTime, LocalDateTime receiverTime, Boolean isEdited) {
        this(msgId, sender, receiver, msg, status, sentTime, delieverdTime, receiverTime, isEdited, false, null);
    }

    public ChatDto(String sender, String receiver, String msg, MessageStatus status, LocalDateTime sentTime,
            LocalDateTime delieverdTime, LocalDateTime receiverTime) {
        this(null, sender, receiver, msg, status, sentTime, delieverdTime, receiverTime, false, false, null);
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public Boolean getIsDeletedForEveryone() {
        return isDeletedForEveryone;
    }

    public void setIsDeletedForEveryone(Boolean isDeletedForEveryone) {
        this.isDeletedForEveryone = isDeletedForEveryone;
    }

    public String getDeletedForUsers() {
        return deletedForUsers;
    }

    public void setDeletedForUsers(String deletedForUsers) {
        this.deletedForUsers = deletedForUsers;
    }

    public Boolean getIsEdited() {
        return isEdited;
    }

    public void setIsEdited(Boolean isEdited) {
        this.isEdited = isEdited;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public LocalDateTime getSentTime() {
        return sentTime;
    }

    public void setSentTime(LocalDateTime sentTime) {
        this.sentTime = sentTime;
    }

    public LocalDateTime getDelieverdTime() {
        return delieverdTime;
    }

    public void setDelieverdTime(LocalDateTime delieverdTime) {
        this.delieverdTime = delieverdTime;
    }

    public LocalDateTime getReceiverTime() {
        return receiverTime;
    }

    public void setReceiverTime(LocalDateTime receiverTime) {
        this.receiverTime = receiverTime;
    }
}
