package com.movie_back.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

/**
 * WebSocket 配置类
 * 用于实时推送通知到前端
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 配置消息代理
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 启用简单的消息代理，用于向客户端发送消息
        // /topic: 用于广播消息
        // /queue: 用于点对点消息
        config.enableSimpleBroker("/topic", "/queue");

        // 设置应用程序目的地前缀
        config.setApplicationDestinationPrefixes("/app");

        // 设置用户目的地前缀（用于点对点消息）
        config.setUserDestinationPrefix("/user");
    }

    /**
     * 注册 STOMP 端点
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册 WebSocket 端点
        registry.addEndpoint("/ws")
                // 允许的跨域源
                .setAllowedOrigins(
                    "http://localhost:8080",
                    "http://localhost:8081",
                    "http://127.0.0.1:8080",
                    "http://127.0.0.1:8081"
                )
                // 启用 SockJS 降级选项
                .withSockJS();
    }
}
