package com.movie_back.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评论点赞记录表
 * 记录用户对评论的点赞关系，防止重复点赞
 */
@Entity
@Table(name = "review_likes",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"review_id", "user_id"})
       },
       indexes = {
           @Index(name = "idx_review_id", columnList = "review_id"),
           @Index(name = "idx_user_id", columnList = "user_id")
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
