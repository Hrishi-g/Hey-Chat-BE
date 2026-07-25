package com.app.chatApp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.chatApp.dto.LastMsgChatDTo;
import com.app.chatApp.vo.HomeMessageList;
import com.app.chatApp.vo.enums.MessageStatus;

import jakarta.transaction.Transactional;

@Repository
public interface HomeMessageListRepo extends JpaRepository<HomeMessageList, Long> {

    @Query("""
            SELECT new com.app.chatApp.dto.LastMsgChatDTo(
            CASE
                WHEN m.sender = :mobNo THEN m.receiver
                ELSE m.sender
            END,
            m.lastMsg,
            m.status,
            m.lastMessageTime
            )
            FROM HomeMessageList m
            WHERE (m.sender = :mobNo OR m.receiver = :mobNo)
            ORDER BY m.lastMessageTime DESC
            """)
    List<LastMsgChatDTo> findLastMsgChatList(@Param("mobNo") String mobNo);

    @Query("""
            SELECT m
            FROM HomeMessageList m
            WHERE (m.sender = :sender AND m.receiver = :receiver)
            OR (m.sender = :receiver AND m.receiver = :sender)
            """)
    Optional<HomeMessageList> checkIfUserExistInHomeMessageChat(@Param("sender") String sender,
            @Param("receiver") String receiver);

    @Modifying
    @Transactional
    @Query("UPDATE HomeMessageList m SET m.status = :status WHERE m.receiver = :receiver AND m.status = :targetStatus")
    int updateStatusByReceiver(
            @Param("receiver") String receiver,
            @Param("targetStatus") MessageStatus targetStatus,
            @Param("status") MessageStatus status);
}
