package com.movie_back.backend.dto.auth;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

// 登录成功后返回的数据传输对象
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String username;
    private String role;
    private String profileImageUrl;
    private String email; // 新增
    private String personalWebsite; // 新增
    private LocalDate birthDate; // 新增
    private String bio; // 新增
}