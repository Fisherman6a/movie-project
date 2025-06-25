package com.movie_back.backend.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PhoneUpdateRequest {
    @NotBlank
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,20}$", message = "无效的电话号码格式")
    private String phone;
}