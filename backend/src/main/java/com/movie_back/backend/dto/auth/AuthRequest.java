package com.movie_back.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String captchaId; // 验证码ID

    @NotBlank
    private String captcha; // 用户输入的验证码
}