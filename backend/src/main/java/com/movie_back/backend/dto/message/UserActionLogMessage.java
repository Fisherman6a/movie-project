package com.movie_back.backend.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 用户行为日志消息
 * 记录用户操作，异步入库
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActionLogMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 操作类型: VIEW, SEARCH, REVIEW, LIKE, LOGIN, LOGOUT
     */
    private String action;

    /**
     * 实体类型: MOVIE, REVIEW, USER
     */
    private String entityType;

    /**
     * 实体ID
     */
    private Long entityId;

    /**
     * 详细信息（JSON格式）
     */
    private Map<String, Object> details;

    /**
     * IP 地址
     */
    private String ipAddress;

    /**
     * User-Agent
     */
    private String userAgent;

    /**
     * 时间戳
     */
    private Long timestamp;

    public UserActionLogMessage(Long userId, String action, String entityType,
                                Long entityId, Map<String, Object> details,
                                String ipAddress, String userAgent) {
        this.userId = userId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.timestamp = System.currentTimeMillis();
    }
}
