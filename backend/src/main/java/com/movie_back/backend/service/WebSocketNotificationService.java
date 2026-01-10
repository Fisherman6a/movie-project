package com.movie_back.backend.service;

import com.movie_back.backend.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * WebSocket 通知服务
 * 负责通过 WebSocket 推送消息到前端
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 向特定用户发送通知
     * @param userId 用户ID
     * @param notification 通知内容
     */
    public void sendNotificationToUser(Long userId, NotificationDTO notification) {
        try {
            // 发送到用户专属队列: /user/{userId}/queue/notifications
            messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                notification
            );
            log.info("发送WebSocket通知: userId={}, type={}", userId, notification.getType());
        } catch (Exception e) {
            log.error("发送WebSocket通知失败: userId={}", userId, e);
        }
    }

    /**
     * 广播通知给所有在线用户
     * @param notification 通知内容
     */
    public void broadcastNotification(NotificationDTO notification) {
        try {
            // 发送到广播主题: /topic/notifications
            messagingTemplate.convertAndSend("/topic/notifications", notification);
            log.info("广播WebSocket通知: type={}", notification.getType());
        } catch (Exception e) {
            log.error("广播WebSocket通知失败", e);
        }
    }
}
