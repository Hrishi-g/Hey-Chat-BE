package com.app.chatApp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.app.chatApp.dto.ChatDto;
import com.app.chatApp.vo.Messages;
import com.app.chatApp.vo.enums.MessageStatus;

import jakarta.transaction.Transactional;

import java.util.Optional;

@Repository
public interface MessagesRepo extends JpaRepository<Messages, Long> {

        @Query("SELECT new com.app.chatApp.dto.ChatDto(COALESCE(m.clientMsgId, CAST(m.msgId AS string)), m.sender, m.receiver, m.msg, m.status, m.createdTime, m.delieverdTime, m.receiverTime, m.isEdited, m.isDeletedForEveryone, m.deletedForUsers) FROM Messages m WHERE m.sender=:mobNO ORDER BY m.createdTime DESC")
        List<ChatDto> findAllBySenderMobNO(@Param("mobNO") String mobNO);

        @Query("""
                        SELECT new com.app.chatApp.dto.ChatDto(COALESCE(m.clientMsgId, CAST(m.msgId AS string)), m.sender, m.receiver, m.msg, m.status, m.createdTime, m.delieverdTime, m.receiverTime, m.isEdited, m.isDeletedForEveryone, m.deletedForUsers)
                        FROM Messages m
                        WHERE (m.sender = :sender AND m.receiver = :receiver)
                        OR (m.sender = :receiver AND m.receiver = :sender)
                        ORDER BY m.msgId ASC
                        """)
        List<ChatDto> findAllChatsBtwnUsers(
                        @Param("sender") String sender,
                        @Param("receiver") String receiver);

        Optional<Messages> findByClientMsgId(String clientMsgId);

        Optional<Messages> findByClientMsgIdAndSender(String clientMsgId, String sender);

        @Query("""
                        SELECT m FROM Messages m
                        WHERE (m.sender = :sender AND m.receiver = :receiver)
                        OR (m.sender = :receiver AND m.receiver = :sender)
                        ORDER BY m.msgId DESC
                        """)
        List<Messages> findLatestMessageBetweenUsers(@Param("sender") String sender, @Param("receiver") String receiver);

        @Query("SELECT DISTINCT m.sender FROM Messages m WHERE m.receiver = :receiver AND m.status = 'SENT'")
        List<String> findUniqueSendersWithSentMessages(@Param("receiver") String receiver);

        @Modifying
        @Transactional
        @Query("UPDATE Messages m SET m.status = :status, m.delieverdTime = :deliveredTime WHERE m.receiver = :receiver AND m.status = :targetStatus")
        int updateStatusByReceiver(
                        @Param("receiver") String receiver,
                        @Param("targetStatus") MessageStatus targetStatus,
                        @Param("status") MessageStatus status,
                        @Param("deliveredTime") LocalDateTime deliveredTime);

        @Modifying
        @Transactional
        @Query("UPDATE Messages m SET m.status = :status, m.receiverTime = :readTime WHERE m.sender = :sender AND m.receiver = :receiver AND (m.status = 'SENT' OR m.status = 'DELIVERED')")
        int updateStatusBySenderAndReceiver(
                        @Param("sender") String sender,
                        @Param("receiver") String receiver,
                        @Param("status") MessageStatus status,
                        @Param("readTime") LocalDateTime readTime);
}
