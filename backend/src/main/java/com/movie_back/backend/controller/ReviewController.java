package com.movie_back.backend.controller;

import com.movie_back.backend.dto.review.ReviewDTO;
import com.movie_back.backend.dto.review.ReviewRequest;
import com.movie_back.backend.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/movies/{movieId}/reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDTO> addReviewToMovie(
            @PathVariable Long movieId,
            @RequestParam Long userId,
            @Valid @RequestBody ReviewRequest reviewRequest) {
        ReviewDTO createdReview = reviewService.addReview(movieId, userId, reviewRequest);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    @GetMapping("/movies/{movieId}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviewsForMovie(@PathVariable Long movieId) {
        List<ReviewDTO> reviews = reviewService.getReviewsForMovie(movieId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/users/{userId}/reviews")
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

    @PostMapping("/reviews/{reviewId}/vote")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDTO> voteOnReview(
            @PathVariable Long reviewId,
            @RequestBody Map<String, String> payload) {
        String direction = payload.get("direction");
        ReviewDTO updatedReview = reviewService.voteOnReview(reviewId, direction);
        return ResponseEntity.ok(updatedReview);
    }

    // API端点以调用存储过程
    @GetMapping("/movies/title/{movieTitle}/reviews_procedure")
    public ResponseEntity<List<ReviewDTO>> getReviewsByMovieTitleProcedure(@PathVariable String movieTitle) {
        List<ReviewDTO> reviews = reviewService.getReviewsByMovieTitleFromProcedure(movieTitle);
        return ResponseEntity.ok(reviews);
    }
}