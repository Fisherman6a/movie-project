package com.movie_back.backend.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 评分更新消息
 * 用户发表评论后，发送此消息到 RabbitMQ，后台异步计算电影平均分
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingUpdateMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 电影ID
     */
    private Long movieId;

    /**
     * 触发时间戳
     */
    private Long timestamp;

    public RatingUpdateMessage(Long movieId) {
        this.movieId = movieId;
        this.timestamp = System.currentTimeMillis();
    }
}
