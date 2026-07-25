package com.app.chatApp.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.app.chatApp.dto.ChatDto;
import com.app.chatApp.dto.LastMsgChatDTo;
import com.app.chatApp.repository.HomeMessageListRepo;
import com.app.chatApp.repository.MessagesRepo;
import com.app.chatApp.repository.RegisteredUsersRepo;
import com.app.chatApp.vo.RegisteredUsers;

@Service
public class UserService {

    private MessagesRepo messagesRepo;
    private RegisteredUsersRepo registeredUsersRepo;
    private HomeMessageListRepo homeMessageListRepo;

    UserService(MessagesRepo messagesRepo, RegisteredUsersRepo registeredUsersRepo,
            HomeMessageListRepo homeMessageListRepo) {
        this.messagesRepo = messagesRepo;
        this.registeredUsersRepo = registeredUsersRepo;
        this.homeMessageListRepo = homeMessageListRepo;
    }

    public ResponseEntity<List<ChatDto>> getChatsBtwnUsers(String sender, String receiver) {
        List<ChatDto> messages = messagesRepo.findAllChatsBtwnUsers(sender, receiver);
        return ResponseEntity.ok(messages);
    }

    public ResponseEntity<?> getHomeMessageChat(String mobNO) {
        List<LastMsgChatDTo> messages = homeMessageListRepo.findLastMsgChatList(mobNO);

        // Extract unique partner mobile numbers
        List<String> partnerMblNos = messages.stream()
                .filter(msg -> msg != null)
                .map(msg -> msg.getChatUser())
                .filter(mbl -> mbl != null)
                .distinct()
                .toList();

        // Bulk fetch users
        List<RegisteredUsers> users = registeredUsersRepo.findByMblNoIn(partnerMblNos);

        // Map mobile number to RegisteredUsers
        Map<String, RegisteredUsers> userMap = users.stream()
                .filter(u -> u != null && u.getMblNo() != null)
                .collect(Collectors.toMap(u -> u.getMblNo(), u -> u));

        // Populate name and image details in DTOs
        for (LastMsgChatDTo msg : messages) {
            RegisteredUsers u = userMap.get(msg.getChatUser());
            if (u != null) {
                msg.setChatUserName(u.getName());
                msg.setImgUrl(u.getImgUrl());
            }
        }

        return ResponseEntity.ok(messages);
    }

    public ResponseEntity<Optional<RegisteredUsers>> getNewUser(String mobNO) {
        Optional<RegisteredUsers> receiver = registeredUsersRepo.findUserByMblNo(mobNO);
        if (receiver.isPresent()) {
            return ResponseEntity.ok().body(receiver);
        } else {
            return ResponseEntity.ok(null);
        }
    }
}
