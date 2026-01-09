package com.movie_back.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务
 * 使用 Redis 存储验证码，支持过期时间
 */
@Service
@RequiredArgsConstructor
public class VerificationCodeService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CODE_PREFIX = "verification:";
    private static final int CODE_LENGTH = 6;
    private static final long CODE_EXPIRATION_MINUTES = 5; // 5分钟过期

    /**
     * 生成并保存验证码
     * @param identifier 标识符（邮箱或手机号）
     * @return 生成的验证码
     */
    public String generateCode(String identifier) {
        // 生成6位随机数字验证码
        String code = generateRandomCode();

        // 保存到 Redis，5分钟过期
        String key = CODE_PREFIX + identifier;
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRATION_MINUTES, TimeUnit.MINUTES);

        return code;
    }

    /**
     * 验证验证码
     * @param identifier 标识符（邮箱或手机号）
     * @param code 用户输入的验证码
     * @return true 如果验证码正确且未过期
     */
    public boolean verifyCode(String identifier, String code) {
        String key = CODE_PREFIX + identifier;
        Object storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            return false; // 验证码不存在或已过期
        }

        boolean isValid = storedCode.toString().equals(code);

        // 验证成功后删除验证码（一次性使用）
        if (isValid) {
            redisTemplate.delete(key);
        }

        return isValid;
    }

    /**
     * 删除验证码
     * @param identifier 标识符（邮箱或手机号）
     */
    public void deleteCode(String identifier) {
        String key = CODE_PREFIX + identifier;
        redisTemplate.delete(key);
    }

    /**
     * 检查验证码是否存在（未过期）
     * @param identifier 标识符
     * @return true 如果存在
     */
    public boolean codeExists(String identifier) {
        String key = CODE_PREFIX + identifier;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 获取验证码剩余有效时间（秒）
     * @param identifier 标识符
     * @return 剩余秒数，不存在返回0
     */
    public long getCodeTTL(String identifier) {
        String key = CODE_PREFIX + identifier;
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return ttl != null ? ttl : 0;
    }

    /**
     * 生成随机6位数字验证码
     */
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
