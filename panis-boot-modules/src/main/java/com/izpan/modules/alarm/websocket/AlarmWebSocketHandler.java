package com.izpan.modules.alarm.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izpan.modules.alarm.domain.vo.AlarmPushVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class AlarmWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;

    private final Map<Long, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    public AlarmWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(session);
            log.info("WebSocket连接建立, userId: {}, sessionId: {}", userId, session.getId());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            Set<WebSocketSession> sessions = userSessions.get(userId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    userSessions.remove(userId);
                }
            }
            log.info("WebSocket连接关闭, userId: {}, sessionId: {}", userId, session.getId());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.debug("收到WebSocket消息: {}", message.getPayload());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误, sessionId: {}", session.getId(), exception);
    }

    public void pushAlarmToUser(Long userId, AlarmPushVO alarmPushVO) {
        Set<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions == null || sessions.isEmpty()) {
            log.debug("用户未连接WebSocket, userId: {}", userId);
            return;
        }

        try {
            String message = objectMapper.writeValueAsString(alarmPushVO);
            TextMessage textMessage = new TextMessage(message);

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(textMessage);
                    log.info("推送报警消息到用户, userId: {}, alarmId: {}", userId, alarmPushVO.getAlarmId());
                }
            }
        } catch (IOException e) {
            log.error("推送报警消息失败, userId: {}", userId, e);
        }
    }

    public void pushAlarmToUsers(Set<Long> userIds, AlarmPushVO alarmPushVO) {
        for (Long userId : userIds) {
            pushAlarmToUser(userId, alarmPushVO);
        }
    }

    public boolean isUserOnline(Long userId) {
        Set<WebSocketSession> sessions = userSessions.get(userId);
        return sessions != null && !sessions.isEmpty();
    }

    private Long getUserIdFromSession(WebSocketSession session) {
        Object userIdAttr = session.getAttributes().get("userId");
        if (userIdAttr instanceof Long) {
            return (Long) userIdAttr;
        }
        return null;
    }
}
