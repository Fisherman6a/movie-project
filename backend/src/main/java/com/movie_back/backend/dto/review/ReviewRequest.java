package com.movie_back.backend.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequest { // 用于创建和更新
    @NotBlank
    private String commentText;

    @NotNull
    @Min(1)
    @Max(10) // 我们的评分是1-10分制
    private Integer score;
}