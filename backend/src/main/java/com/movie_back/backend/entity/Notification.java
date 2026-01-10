package com.movie_back.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 通知实体
 * 用于存储用户通知（点赞、评论、系统通知等）
 *
 * 业务场景：
 * 1. LIKE - 评论被点赞时通知评论作者
 * 2. COMMENT - 电影有新评论时通知相关用户
 * 3. SYSTEM - 系统通知
 */
@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_is_read", columnList = "isRead"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 接收通知的用户ID
     * 对应 User 实体的 id
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 通知类型枚举
     * LIKE - 点赞通知
     * COMMENT - 评论通知
     * SYSTEM - 系统通知
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    /**
     * 通知标题
     * 例如: "评论点赞", "新评论", "系统消息"
     */
    @Column(nullable = false, length = 255)
    private String title;

    /**
     * 通知内容
     * 例如: "用户张三赞了你对《阿凡达》的评论"
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 关联的实体ID
     * - 如果是点赞通知，这里存 reviewId
     * - 如果是评论通知，这里存 movieId
     */
    @Column(name = "related_id")
    private Long relatedId;

    /**
     * 关联的实体类型
     * REVIEW - 评论
     * MOVIE - 电影
     * USER - 用户
     */
    @Column(name = "related_type", length = 50)
    private String relatedType;

    /**
     * 触发通知的用户ID（可选）
     * 例如：点赞者的 userId
     */
    @Column(name = "trigger_user_id")
    private Long triggerUserId;

    /**
     * 触发通知的用户名（可选，用于显示）
     * 例如：点赞者的用户名
     */
    @Column(name = "trigger_username", length = 100)
    private String triggerUsername;

    /**
     * 是否已读
     */
    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    /**
     * 创建时间（自动设置）
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 已读时间
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    /**
     * JPA 生命周期回调 - 插入前自动设置创建时间
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isRead == null) {
            isRead = false;
        }
    }

    /**
     * 标记为已读
     */
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    /**
     * 通知类型枚举
     */
    public enum NotificationType {
        LIKE("点赞通知"),
        COMMENT("评论通知"),
        SYSTEM("系统通知");

        private final String description;

        NotificationType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
