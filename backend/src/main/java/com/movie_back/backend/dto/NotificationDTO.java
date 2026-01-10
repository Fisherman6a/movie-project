package com.movie_back.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知消息 DTO
 * 用于 WebSocket 推送和 API 返回
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    /**
     * 通知ID
     */
    private Long id;

    /**
     * 通知类型: LIKE, COMMENT, SYSTEM
     */
    private String type;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String message;

    /**
     * 关联ID（评论ID/电影ID等）
     */
    private Long relatedId;

    /**
     * 关联类型（MOVIE, REVIEW）
     */
    private String relatedType;

    /**
     * 触发通知的用户ID（点赞者等）
     */
    private Long triggerUserId;

    /**
     * 触发通知的用户名
     */
    private String triggerUsername;

    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 时间戳（毫秒）
     */
    private Long timestamp;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 已读时间
     */
    private LocalDateTime readAt;

    /**
     * 简化构造函数 - 用于 WebSocket 推送
     */
    public NotificationDTO(String type, String title, String message, Long relatedId, String relatedType) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.relatedId = relatedId;
        this.relatedType = relatedType;
        this.timestamp = System.currentTimeMillis();
    }
}
