package com.movie_back.backend.service;

import com.movie_back.backend.config.RabbitMQConfig;
import com.movie_back.backend.dto.message.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 消息生产者服务
 * 负责发送消息到 RabbitMQ
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducerService {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送评分更新消息
     * 用户发表评论后，异步计算电影平均分
     */
    public void sendRatingUpdateMessage(Long movieId) {
        try {
            RatingUpdateMessage message = new RatingUpdateMessage(movieId);
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.MOVIE_EXCHANGE,
                RabbitMQConfig.RATING_UPDATE_ROUTING_KEY,
                message
            );
            log.info("发送评分更新消息: movieId={}", movieId);
        } catch (Exception e) {
            log.error("发送评分更新消息失败: movieId={}", movieId, e);
        }
    }

    /**
     * 发送点赞通知消息
     * 评论被点赞时，通知评论作者
     */
    public void sendLikeNotification(Long reviewId, Long authorId, Long likerId,
                                    String likerName, Long movieId, String movieTitle) {
        try {
            LikeNotificationMessage message = new LikeNotificationMessage(
                reviewId, authorId, likerId, likerName, movieId, movieTitle
            );
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.MOVIE_EXCHANGE,
                RabbitMQConfig.LIKE_NOTIFICATION_ROUTING_KEY,
                message
            );
            log.info("发送点赞通知消息: reviewId={}, authorId={}", reviewId, authorId);
        } catch (Exception e) {
            log.error("发送点赞通知消息失败: reviewId={}", reviewId, e);
        }
    }

    /**
     * 发送索引更新消息
     * 电影数据变更后，异步更新 Elasticsearch 索引
     */
    public void sendIndexUpdateMessage(Long movieId, String operation) {
        try {
            IndexUpdateMessage message = new IndexUpdateMessage(movieId, operation);
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.MOVIE_EXCHANGE,
                RabbitMQConfig.INDEX_UPDATE_ROUTING_KEY,
                message
            );
            log.info("发送索引更新消息: movieId={}, operation={}", movieId, operation);
        } catch (Exception e) {
            log.error("发送索引更新消息失败: movieId={}", movieId, e);
        }
    }

    /**
     * 发送用户行为日志消息
     * 记录用户操作，异步入库
     */
    public void sendUserActionLog(Long userId, String action, String entityType,
                                  Long entityId, Map<String, Object> details,
                                  String ipAddress, String userAgent) {
        try {
            UserActionLogMessage message = new UserActionLogMessage(
                userId, action, entityType, entityId, details, ipAddress, userAgent
            );
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.MOVIE_EXCHANGE,
                RabbitMQConfig.LOG_COLLECT_ROUTING_KEY,
                message
            );
            log.debug("发送用户行为日志: userId={}, action={}", userId, action);
        } catch (Exception e) {
            log.error("发送用户行为日志失败: userId={}, action={}", userId, action, e);
        }
    }
}
