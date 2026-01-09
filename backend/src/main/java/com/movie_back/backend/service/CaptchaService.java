package com.movie_back.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 图形验证码服务
 */
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CAPTCHA_PREFIX = "captcha:";
    private static final int CAPTCHA_EXPIRATION_MINUTES = 5; // 5分钟过期
    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int CODE_LENGTH = 4;

    // 验证码字符集（去除容易混淆的字符：0 O o, 1 I l）
    private static final String CHARACTERS = "23456789ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";

    /**
     * 生成图形验证码
     * @return Map {captchaId: 验证码ID, imageBase64: Base64编码的图片}
     */
    public CaptchaResult generateCaptcha() {
        // 生成随机验证码文本
        String code = generateRandomCode();

        // 生成唯一 ID
        String captchaId = UUID.randomUUID().toString();

        // 保存到 Redis（不区分大小写）
        redisTemplate.opsForValue().set(
            CAPTCHA_PREFIX + captchaId,
            code.toLowerCase(),
            CAPTCHA_EXPIRATION_MINUTES,
            TimeUnit.MINUTES
        );

        // 生成验证码图片
        String imageBase64 = generateCaptchaImage(code);

        return new CaptchaResult(captchaId, imageBase64);
    }

    /**
     * 验证验证码
     * @param captchaId 验证码ID
     * @param userInput 用户输入的验证码
     * @return true 验证成功
     */
    public boolean verifyCaptcha(String captchaId, String userInput) {
        if (captchaId == null || userInput == null) {
            return false;
        }

        String key = CAPTCHA_PREFIX + captchaId;
        Object storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            return false; // 验证码不存在或已过期
        }

        boolean isValid = storedCode.toString().equalsIgnoreCase(userInput.trim());

        // 验证后删除验证码（一次性使用）
        if (isValid) {
            redisTemplate.delete(key);
        }

        return isValid;
    }

    /**
     * 生成随机验证码文本
     */
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }

    /**
     * 生成验证码图片并转为 Base64
     */
    private String generateCaptchaImage(String code) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Random random = new Random();

        // 背景色
        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 绘制干扰线
        for (int i = 0; i < 6; i++) {
            g.setColor(getRandomColor(180, 230));
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        // 绘制验证码字符
        g.setFont(new Font("Arial", Font.BOLD, 28));
        for (int i = 0; i < CODE_LENGTH; i++) {
            g.setColor(getRandomColor(20, 130));
            char c = code.charAt(i);
            int x = 20 + i * 25;
            int y = 25 + random.nextInt(10);
            g.drawString(String.valueOf(c), x, y);
        }

        // 绘制干扰点
        for (int i = 0; i < 50; i++) {
            g.setColor(getRandomColor(150, 200));
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            g.fillOval(x, y, 2, 2);
        }

        g.dispose();

        // 转为 Base64
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("生成验证码图片失败", e);
        }
    }

    /**
     * 获取随机颜色
     */
    private Color getRandomColor(int min, int max) {
        Random random = new Random();
        int r = min + random.nextInt(max - min);
        int g = min + random.nextInt(max - min);
        int b = min + random.nextInt(max - min);
        return new Color(r, g, b);
    }

    /**
     * 验证码结果类
     */
    public static class CaptchaResult {
        private final String captchaId;
        private final String imageBase64;

        public CaptchaResult(String captchaId, String imageBase64) {
            this.captchaId = captchaId;
            this.imageBase64 = imageBase64;
        }

        public String getCaptchaId() {
            return captchaId;
        }

        public String getImageBase64() {
            return imageBase64;
        }
    }
}
