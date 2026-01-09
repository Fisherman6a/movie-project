package com.movie_back.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Token 管理服务
 * 使用 Redis 管理 JWT Token 的生命周期
 */
@Service
@RequiredArgsConstructor
public class TokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_PREFIX = "token:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    /**
     * 保存 token 到 Redis
     * @param token JWT token
     * @param userId 用户ID
     * @param expirationMs 过期时间（毫秒）
     */
    public void saveToken(String token, Long userId, long expirationMs) {
        String key = TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, userId, expirationMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 验证 token 是否有效
     * @param token JWT token
     * @return true 如果 token 存在且未过期
     */
    public boolean isTokenValid(String token) {
        String key = TOKEN_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key)) && !isTokenBlacklisted(token);
    }

    /**
     * 获取 token 对应的用户ID
     * @param token JWT token
     * @return 用户ID，如果不存在返回null
     */
    public Long getUserIdByToken(String token) {
        String key = TOKEN_PREFIX + token;
        Object userId = redisTemplate.opsForValue().get(key);
        return userId != null ? Long.valueOf(userId.toString()) : null;
    }

    /**
     * 删除 token（用户登出）
     * @param token JWT token
     */
    public void deleteToken(String token) {
        String key = TOKEN_PREFIX + token;
        redisTemplate.delete(key);
    }

    /**
     * 将 token 加入黑名单
     * @param token JWT token
     * @param expirationMs 过期时间（毫秒）
     */
    public void addToBlacklist(String token, long expirationMs) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "blacklisted", expirationMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 检查 token 是否在黑名单中
     * @param token JWT token
     * @return true 如果在黑名单中
     */
    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 刷新 token 过期时间
     * @param token JWT token
     * @param expirationMs 新的过期时间（毫秒）
     */
    public void refreshToken(String token, long expirationMs) {
        String key = TOKEN_PREFIX + token;
        redisTemplate.expire(key, expirationMs, TimeUnit.MILLISECONDS);
    }
}
