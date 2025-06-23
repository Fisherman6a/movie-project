package com.movie_back.backend.dto.user;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserProfileUpdateDTO {
    private String username;
    private String personalWebsite;
    private LocalDate birthDate;
    private String bio;
    // 注意：密码和头像将通过单独的接口处理
    private String profileImageUrl; // 用于接收前端传来的图床URL
}