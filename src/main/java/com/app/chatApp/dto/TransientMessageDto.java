package com.app.chatApp.dto;

import com.app.chatApp.vo.enums.MessageStatus;

public class TransientMessageDto {
    private String msgId;
    private String type;
    private String sender;
    private String receiver;
    private String message;
    private long timeStamp;
    private MessageStatus status;
    private Boolean isEdited = false;
    private Boolean isDeletedForEveryone = false;
    private String deletedForUsers;

    public TransientMessageDto() {
    }

    public TransientMessageDto(String type, String sender, String receiver, String message) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timeStamp = System.currentTimeMillis();
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public Boolean getIsEdited() {
        return isEdited;
    }

    public void setIsEdited(Boolean isEdited) {
        this.isEdited = isEdited;
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

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

}
