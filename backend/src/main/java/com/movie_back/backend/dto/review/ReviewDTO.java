package com.movie_back.backend.dto.review;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewDTO {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private Long userId;
    private String username;
    private String userProfileImageUrl;
    private String commentText;
    private LocalDateTime createdAt;
    // private LocalDateTime updatedAt;
    private Integer score;
    private Integer likes;
}