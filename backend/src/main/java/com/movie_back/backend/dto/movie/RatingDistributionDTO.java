package com.movie_back.backend.dto.movie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingDistributionDTO {
    private int star; // 星级 (1-5)
    private long count; // 评分人数
    private double percentage; // 百分比
}