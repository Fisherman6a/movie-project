package com.movie_back.backend.dto.auth;

import java.time.LocalDate;
import java.time.LocalDateTime; // 新增导入
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String username;
    private String role;
    private String profileImageUrl;
    private String email;
    private String personalWebsite;
    private LocalDate birthDate;
    private String bio;
    private LocalDateTime createdAt; // <-- 新增此字段
}
