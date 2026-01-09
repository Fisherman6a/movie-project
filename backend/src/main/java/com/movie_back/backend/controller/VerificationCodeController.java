package com.movie_back.backend.controller;

import com.movie_back.backend.service.CaptchaService;
import com.movie_back.backend.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 验证码控制器
 */
@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
public class VerificationCodeController {

    private final VerificationCodeService verificationCodeService;
    private final CaptchaService captchaService;

    /**
     * 生成图形验证码（用于登录）
     * @return 验证码ID和Base64图片
     */
    @GetMapping("/captcha")
    public ResponseEntity<Map<String, String>> generateCaptcha() {
        CaptchaService.CaptchaResult result = captchaService.generateCaptcha();

        Map<String, String> response = new HashMap<>();
        response.put("captchaId", result.getCaptchaId());
        response.put("image", result.getImageBase64());

        return ResponseEntity.ok(response);
    }

    /**
     * 生成验证码
     * @param identifier 邮箱或手机号
     * @return 验证码（仅用于开发测试，生产环境应通过短信/邮件发送）
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateCode(@RequestParam String identifier) {
        // 检查是否已存在未过期的验证码
        if (verificationCodeService.codeExists(identifier)) {
            long ttl = verificationCodeService.getCodeTTL(identifier);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "验证码已发送，请在" + ttl + "秒后重试");
            return ResponseEntity.badRequest().body(response);
        }

        // 生成验证码
        String code = verificationCodeService.generateCode(identifier);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "验证码已生成");
        // 注意：生产环境中不应返回验证码，应通过短信或邮件发送
        // 这里仅用于开发测试
        response.put("code", code); // 开发环境返回，生产环境删除这行
        response.put("expiresIn", 300); // 5分钟

        return ResponseEntity.ok(response);
    }

    /**
     * 验证验证码
     * @param identifier 邮箱或手机号
     * @param code 用户输入的验证码
     * @return 验证结果
     */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyCode(
            @RequestParam String identifier,
            @RequestParam String code) {

        boolean isValid = verificationCodeService.verifyCode(identifier, code);

        Map<String, Object> response = new HashMap<>();
        response.put("success", isValid);
        response.put("message", isValid ? "验证成功" : "验证码错误或已过期");

        return ResponseEntity.ok(response);
    }

    /**
     * 删除验证码（可选）
     * @param identifier 邮箱或手机号
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteCode(@RequestParam String identifier) {
        verificationCodeService.deleteCode(identifier);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "验证码已删除");

        return ResponseEntity.ok(response);
    }
}
