package com.app.chatApp.dto;

public class GetChatsRequestDto {

    private String receiver;

    public GetChatsRequestDto() {
    }

    public GetChatsRequestDto(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
