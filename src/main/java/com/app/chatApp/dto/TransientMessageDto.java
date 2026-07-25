package com.app.chatApp.dto;

public class TransientMessageDto {
    private String type;
    private String sender;
    private String receiver;
    private String message;
    private long timeStamp;
    private String status;

    public TransientMessageDto() {
    }

    public TransientMessageDto(String type, String sender, String receiver, String message) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timeStamp = System.currentTimeMillis();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
