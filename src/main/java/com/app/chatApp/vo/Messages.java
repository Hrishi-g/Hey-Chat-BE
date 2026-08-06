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
public class Messages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long msgId;
    String sender;
    String receiver;
    @ColumnTransformer(read = "pgp_sym_decrypt(msg, '" + "${chat.secret}" + "')", write = "pgp_sym_encrypt(?::text, '"
            + "${chat.secret}" + "', 'cipher-algo=aes256, compress-algo=2')")
    @Column(columnDefinition = "bytea", nullable = false)
    String msg;
    @Enumerated(EnumType.STRING)
    MessageStatus status;
    LocalDateTime createdTime = LocalDateTime.now();
    LocalDateTime sentTime;
    LocalDateTime delieverdTime;
    LocalDateTime receiverTime;

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

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
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

    @Override
    public String toString() {
        return "Messages [msgId=" + msgId + ", senderId=" + sender + ", receiverId=" + receiver + ", msg=" + msg
                + ", status=" + status + ", createdTime=" + createdTime + ", sentTime=" + sentTime + ", delieverdTime="
                + delieverdTime + ", receiverTime=" + receiverTime + "]";
    }
}
