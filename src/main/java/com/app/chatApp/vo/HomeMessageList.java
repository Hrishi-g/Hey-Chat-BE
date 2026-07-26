package com.app.chatApp.vo;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnTransformer;

import com.app.chatApp.vo.enums.MessageStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class HomeMessageList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long msgId;
    String sender;
    String receiver;
    @ColumnTransformer(read = "pgp_sym_decrypt(last_msg, current_setting('chat.secret'))", write = "pgp_sym_encrypt(?::text, current_setting('chat.secret'), 'cipher-algo=aes256, compress-algo=2')")
    @Column(columnDefinition = "bytea", nullable = false)
    String lastMsg;
    @Enumerated(EnumType.STRING)
    MessageStatus status;
    LocalDateTime lastMessageTime = LocalDateTime.now();

    public Long getMsgId() {
        return msgId;
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
