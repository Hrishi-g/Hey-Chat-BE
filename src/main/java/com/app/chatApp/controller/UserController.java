package com.app.chatApp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.chatApp.dto.ChatDto;
import com.app.chatApp.dto.GetChatsRequestDto;
import com.app.chatApp.dto.SecurityContextDto;
import com.app.chatApp.dto.UserDto;
import com.app.chatApp.service.UserService;
import com.app.chatApp.vo.RegisteredUsers;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile(@AuthenticationPrincipal SecurityContextDto user) {
        UserDto tempUser = userService.getProfile(user.getUserId());
        if (tempUser != null) {
            return ResponseEntity.ok(tempUser);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/profile/update")
    public ResponseEntity<UserDto> updateProfile(@AuthenticationPrincipal SecurityContextDto user,
            @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateProfile(user.getUserId(), userDto);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/chats")
    public ResponseEntity<List<ChatDto>> getChats(@AuthenticationPrincipal SecurityContextDto userData,
            @RequestBody GetChatsRequestDto req) {
        return userService.getChatsBtwnUsers(userData.getMblNo(), req.getReceiver());
    }

    @PostMapping("/getNewUser")
    public ResponseEntity<Optional<RegisteredUsers>> getNewUser(@RequestBody GetChatsRequestDto req) {
        return userService.getNewUser(req.getReceiver());
    }

    @GetMapping("/allHomeChats")
    public ResponseEntity<?> getAllHomeChats(@AuthenticationPrincipal SecurityContextDto userData) {
        return userService.getHomeMessageChat(userData.getMblNo());
    }
}