package com.app.chatApp.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
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
            session.close(org.springframework.web.socket.CloseStatus.BAD_DATA);
            return;
        }

        session.getAttributes().put("mblNo", mblNo);

        users.put(mblNo, session);

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

    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        TransientMessageDto msg = objectMapper.readValue(message.getPayload(), TransientMessageDto.class);

        String sessionMblNo = (String) session.getAttributes().get("mblNo");

        if (sessionMblNo != null && sessionMblNo.equals(msg.getSender())) {
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
            msg.setType("CHAT");
            WebSocketSession receiverSession = users.get(msg.getReceiver());
            // Both Online
            if (receiverSession != null && receiverSession.isOpen()) {
                // save message with delivered status
                utilityHandler.saveMessage(msg, MessageStatus.DELIVERED);
                // updating last msg
                utilityHandler.updateHomeMessageList(msg, MessageStatus.DELIVERED);

                msg.setStatus("DELIVERED");
                String chatJson = objectMapper.writeValueAsString(msg);
                receiverSession.sendMessage(new TextMessage(chatJson));
                if (!receiverSession.equals(session)) {
                    session.sendMessage(new TextMessage(chatJson));
                }
            }
            // sender Online , Receiver Offline
            else {
                // save message with sent status
                utilityHandler.saveMessage(msg, MessageStatus.SENT);
                // updating last msg
                utilityHandler.updateHomeMessageList(msg, MessageStatus.SENT);

                msg.setStatus("SENT");
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
        }
    }
}
