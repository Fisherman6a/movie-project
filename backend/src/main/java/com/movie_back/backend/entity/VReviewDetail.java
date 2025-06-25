package com.movie_back.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Entity
@Immutable // 标记为只读实体，JPA不会尝试更新它
@Table(name = "v_review_details")
@Data
public class VReviewDetail {

    @Id
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "comment_text")
    private String commentText;

    @Column(name = "score")
    private Integer score;

    @Column(name = "likes")
    private Integer likes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username")
    private String username;

    @Column(name = "movie_id")
    private Long movieId;

    @Column(name = "movie_title")
    private String movieTitle;
}