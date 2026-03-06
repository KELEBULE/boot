package com.izpan.modules.alarm.websocket;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class AlarmWebSocketConfig implements WebSocketConfigurer {

    private final AlarmWebSocketHandler alarmWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(alarmWebSocketHandler, "/ws/alarm")
                .addInterceptors(new AlarmHandshakeInterceptor())
                .setAllowedOrigins("*");
    }

    private static class AlarmHandshakeInterceptor implements HandshakeInterceptor {

        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

            log.info("WebSocket握手开始, URI: {}", request.getURI());

            if (request instanceof ServletServerHttpRequest servletRequest) {
                String token = servletRequest.getServletRequest().getParameter("token");
                log.info("WebSocket握手token: {}", token != null ? "存在" : "不存在");

                if (token != null && !token.isEmpty()) {
                    try {
                        Object loginId = StpUtil.getLoginIdByToken(token);
                        log.info("WebSocket握手loginId: {}", loginId);
                        if (loginId != null) {
                            attributes.put("userId", Long.parseLong(loginId.toString()));
                            log.info("WebSocket握手成功, userId: {}", loginId);
                            return true;
                        }
                    } catch (Exception e) {
                        log.error("WebSocket握手验证失败: {}", e.getMessage(), e);
                        return false;
                    }
                }
            }
            log.warn("WebSocket握手失败: 无效的请求或token");
            return false;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                WebSocketHandler wsHandler, Exception exception) {
            if (exception != null) {
                log.error("WebSocket握手后异常: {}", exception.getMessage(), exception);
            } else {
                log.info("WebSocket握手完成");
            }
        }
    }
}
