package com.movie_back.backend.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 索引更新消息
 * 电影数据变更后，发送此消息异步更新 Elasticsearch 索引
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndexUpdateMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 电影ID
     */
    private Long movieId;

    /**
     * 操作类型: CREATE, UPDATE, DELETE
     */
    private String operation;

    /**
     * 时间戳
     */
    private Long timestamp;

    public IndexUpdateMessage(Long movieId, String operation) {
        this.movieId = movieId;
        this.operation = operation;
        this.timestamp = System.currentTimeMillis();
    }
}
