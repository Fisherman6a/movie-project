package com.movie_back.backend.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 点赞通知消息
 * 评论被点赞时，发送此消息通知评论作者
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeNotificationMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
    private Long reviewId;

    /**
     * 评论作者ID（接收通知的用户）
     */
    private Long authorId;

    /**
     * 点赞用户ID
     */
    private Long likerId;

    /**
     * 点赞用户名
     */
    private String likerName;

    /**
     * 电影ID
     */
    private Long movieId;

    /**
     * 电影标题
     */
    private String movieTitle;

    /**
     * 时间戳
     */
    private Long timestamp;

    public LikeNotificationMessage(Long reviewId, Long authorId, Long likerId,
                                   String likerName, Long movieId, String movieTitle) {
        this.reviewId = reviewId;
        this.authorId = authorId;
        this.likerId = likerId;
        this.likerName = likerName;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.timestamp = System.currentTimeMillis();
    }
}
