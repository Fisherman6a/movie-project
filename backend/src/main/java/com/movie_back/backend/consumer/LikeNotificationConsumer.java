package com.movie_back.backend.consumer;

import com.movie_back.backend.config.RabbitMQConfig;
import com.movie_back.backend.dto.message.LikeNotificationMessage;
import com.movie_back.backend.entity.Notification;
import com.movie_back.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 点赞通知消费者
 * 监听点赞通知消息，保存到数据库（不发送WebSocket弹窗）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LikeNotificationConsumer {

    private final NotificationRepository notificationRepository;

    @RabbitListener(queues = RabbitMQConfig.LIKE_NOTIFICATION_QUEUE)
    public void handleLikeNotification(LikeNotificationMessage message) {
        try {
            log.info("收到点赞通知消息: reviewId={}, authorId={}", message.getReviewId(), message.getAuthorId());

            // 构建通知消息
            String notificationMessage = String.format(
                "用户 %s 赞了你对《%s》的评论",
                message.getLikerName(),
                message.getMovieTitle()
            );

            // 保存通知到数据库（使用 Builder 模式）
            Notification notification = Notification.builder()
                .userId(message.getAuthorId())
                .type(Notification.NotificationType.LIKE)
                .title("评论点赞")
                .content(notificationMessage)
                .relatedId(message.getReviewId())
                .relatedType("REVIEW")
                .triggerUserId(message.getLikerId())
                .triggerUsername(message.getLikerName())
                .build();

            notificationRepository.save(notification);
            log.info("通知已保存到数据库: notificationId={}", notification.getId());

        } catch (Exception e) {
            log.error("处理点赞通知消息失败: reviewId={}", message.getReviewId(), e);
            // 使用自动ACK，异常会导致消息重新入队
            throw new RuntimeException("处理点赞通知失败", e);
        }
    }
}
