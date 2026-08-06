package com.app.chatApp.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.app.chatApp.dto.TransientMessageDto;
import com.app.chatApp.service.OneTimeTicketService;
import com.app.chatApp.vo.enums.MessageStatus;

import tools.jackson.databind.ObjectMapper;

@Component
public class ChatHandler extends TextWebSocketHandler {

    private final OneTimeTicketService ticketService;
    private UtilityHandler utilityHandler;
    private final ObjectMapper objectMapper;

    ChatHandler(OneTimeTicketService ticketService, UtilityHandler utilityHandler, ObjectMapper objectMapper) {
        this.ticketService = ticketService;
        this.utilityHandler = utilityHandler;
        this.objectMapper = objectMapper;
    }

    Map<String, WebSocketSession> users = new ConcurrentHashMap<>();

    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String ticket = session.getUri().getQuery().split("=")[1];

        String mblNo = ticketService.redeemTicket(ticket);
        if (mblNo == null) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        session.getAttributes().put("mblNo", mblNo);

        users.put(mblNo, session);

        // Send list of currently online users to the connected user
        try {
            Map<String, Object> onlineListEvent = new HashMap<>();
            onlineListEvent.put("type", "ONLINE_USERS_LIST");
            onlineListEvent.put("users", users.keySet());
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(onlineListEvent)));
        } catch (Exception e) {
            System.err.println("Failed to send online users list to " + mblNo + ": " + e.getMessage());
        }

        // Broadcast to other users that this user is online
        broadcastUserStatus(mblNo, "ONLINE");

        try {
            List<String> sendersToNotify = utilityHandler.deliverPendingMessages(mblNo);
            for (String senderMblNo : sendersToNotify) {
                WebSocketSession senderSession = users.get(senderMblNo);
                if (senderSession != null && senderSession.isOpen()) {
                    Map<String, Object> updateEvent = new HashMap<>();
                    updateEvent.put("type", "STATUS_UPDATE");
                    updateEvent.put("sender", senderMblNo);
                    updateEvent.put("receiver", mblNo);
                    updateEvent.put("status", "DELIVERED");
                    senderSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(updateEvent)));
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to update and notify of pending messages for " + mblNo + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void broadcastUserStatus(String mblNo, String status) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "USER_STATUS");
        event.put("user", mblNo);
        event.put("status", status);
        try {
            String json = objectMapper.writeValueAsString(event);
            TextMessage textMessage = new TextMessage(json);
            for (Map.Entry<String, WebSocketSession> entry : users.entrySet()) {
                if (!entry.getKey().equals(mblNo)) {
                    WebSocketSession wsSession = entry.getValue();
                    if (wsSession != null && wsSession.isOpen()) {
                        wsSession.sendMessage(textMessage);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to broadcast user status: " + e.getMessage());
        }
    }

    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        TransientMessageDto msg = objectMapper.readValue(message.getPayload(), TransientMessageDto.class);

        String sessionMblNo = (String) session.getAttributes().get("mblNo");

        if (sessionMblNo != null && sessionMblNo.equals(msg.getSender())) {
            if ("PING".equals(msg.getType())) {
                return;
            }
            if ("READ".equals(msg.getType())) {
                utilityHandler.markMessagesAsRead(msg.getReceiver(), msg.getSender());
                WebSocketSession originalSenderSession = users.get(msg.getReceiver());
                if (originalSenderSession != null && originalSenderSession.isOpen()) {
                    Map<String, Object> updateEvent = new HashMap<>();
                    updateEvent.put("type", "STATUS_UPDATE");
                    updateEvent.put("sender", msg.getReceiver());
                    updateEvent.put("receiver", msg.getSender());
                    updateEvent.put("status", "READ");
                    originalSenderSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(updateEvent)));
                }
                return;
            }
            if (msg.getSender() != null && msg.getSender().equals(msg.getReceiver())) {
                msg.setStatus(MessageStatus.READ);
                utilityHandler.saveMessage(msg);
                utilityHandler.updateHomeMessageList(msg);

                msg.setType("CHAT");
                msg.setStatus(MessageStatus.READ);
                String chatJson = objectMapper.writeValueAsString(msg);
                session.sendMessage(new TextMessage(chatJson));
                return;
            }

            msg.setType("CHAT");
            WebSocketSession receiverSession = users.get(msg.getReceiver());
            // Both Online
            if (receiverSession != null && receiverSession.isOpen()) {
                // save message with delivered status
                msg.setStatus(MessageStatus.DELIVERED);
                utilityHandler.saveMessage(msg);
                // updating last msg
                utilityHandler.updateHomeMessageList(msg);

                msg.setStatus(MessageStatus.DELIVERED);
                String chatJson = objectMapper.writeValueAsString(msg);
                receiverSession.sendMessage(new TextMessage(chatJson));
                if (!receiverSession.equals(session)) {
                    session.sendMessage(new TextMessage(chatJson));
                }
            }
            // sender Online , Receiver Offline
            else {
                // save message with sent status
                msg.setStatus(MessageStatus.SENT);
                utilityHandler.saveMessage(msg);
                // updating last msg
                utilityHandler.updateHomeMessageList(msg);

                msg.setStatus(MessageStatus.SENT);
                String chatJson = objectMapper.writeValueAsString(msg);
                session.sendMessage(new TextMessage(chatJson));
            }
        } else {
            // System.out.println(
            // "Sender mismatch or unauthorized: msg sender=" + msg.getSender() + ",
            // session=" + sessionMblNo);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status)
            throws Exception {
        String mblNo = (String) session.getAttributes().get("mblNo");
        if (mblNo != null) {
            users.remove(mblNo);
            broadcastUserStatus(mblNo, "OFFLINE");
        }
    }
}
