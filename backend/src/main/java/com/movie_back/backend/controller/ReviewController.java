package com.movie_back.backend.controller;

import com.movie_back.backend.dto.review.ReviewDTO;
import com.movie_back.backend.dto.review.ReviewRequest;
import com.movie_back.backend.entity.User;
import com.movie_back.backend.repository.UserRepository;
import com.movie_back.backend.service.MessageProducerService;
import com.movie_back.backend.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final MessageProducerService messageProducer;
    private final UserRepository userRepository;

    @PostMapping("/movies/{movieId}/reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDTO> addReviewToMovie(
            @PathVariable Long movieId,
            @RequestParam Long userId,
            @Valid @RequestBody ReviewRequest reviewRequest) {
        // 1. 保存评论
        ReviewDTO createdReview = reviewService.addReview(movieId, userId, reviewRequest);

        // 2. 发送 RabbitMQ 消息 - 异步计算电影评分
        messageProducer.sendRatingUpdateMessage(movieId);

        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    @GetMapping("/movies/{movieId}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviewsForMovie(@PathVariable Long movieId) {
        List<ReviewDTO> reviews = reviewService.getReviewsForMovie(movieId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/users/{userId}/reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReviewDTO>> getReviewsByUser(@PathVariable Long userId) {
        List<ReviewDTO> reviews = reviewService.getReviewsForUser(userId);
        return ResponseEntity.ok(reviews);
    }

    @DeleteMapping("/reviews/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reviews/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest reviewRequest) {
        ReviewDTO updatedReview = reviewService.updateReview(reviewId, reviewRequest);
        return ResponseEntity.ok(updatedReview);
    }

    @GetMapping("/reviews")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        List<ReviewDTO> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long reviewId) {
        ReviewDTO review = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(review);
    }

    @PostMapping("/reviews/{reviewId}/vote")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDTO> voteOnReview(
            @PathVariable Long reviewId,
            @RequestParam Long likerId,
            @RequestBody Map<String, String> payload) {
        String direction = payload.get("direction");

        try {
            // 调用新的voteOnReview方法，传入userId
            ReviewDTO updatedReview = reviewService.voteOnReview(reviewId, likerId, direction);

            // 如果是点赞，发送通知消息到RabbitMQ（仅更新数据库，不弹窗）
            if ("up".equalsIgnoreCase(direction)) {
                User liker = userRepository.findById(likerId)
                        .orElseThrow(() -> new RuntimeException("用户不存在"));

                messageProducer.sendLikeNotification(
                        reviewId,
                        updatedReview.getUserId(),  // 评论作者ID
                        likerId,
                        liker.getUsername(),
                        updatedReview.getMovieId(),
                        updatedReview.getMovieTitle()
                );
            }

            return ResponseEntity.ok(updatedReview);
        } catch (IllegalStateException e) {
            // 返回友好的错误信息（重复点赞或未点赞）
            return ResponseEntity.badRequest().body(null);
        }
    }

    // API端点以调用存储过程
    @GetMapping("/movies/title/{movieTitle}/reviews_procedure")
    public ResponseEntity<List<ReviewDTO>> getReviewsByMovieTitleProcedure(@PathVariable String movieTitle) {
        List<ReviewDTO> reviews = reviewService.getReviewsByMovieTitleFromProcedure(movieTitle);
        return ResponseEntity.ok(reviews);
    }
}
