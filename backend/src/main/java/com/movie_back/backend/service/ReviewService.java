package com.movie_back.backend.service;

import com.movie_back.backend.dto.review.ReviewDTO;
import com.movie_back.backend.dto.review.ReviewRequest;
import com.movie_back.backend.entity.Movie;
import com.movie_back.backend.entity.Review;
import com.movie_back.backend.entity.User;
import com.movie_back.backend.exception.ResourceNotFoundException;
import com.movie_back.backend.repository.MovieRepository;
import com.movie_back.backend.repository.ReviewRepository;
import com.movie_back.backend.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final MovieService movieService;

    public ReviewService(ReviewRepository reviewRepository, MovieRepository movieRepository,
            UserRepository userRepository, @Lazy MovieService movieService) {
        this.reviewRepository = reviewRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.movieService = movieService;
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::convertToReviewDTO)
                .sorted(Comparator.comparing(ReviewDTO::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsForMovie(Long movieId) {
        List<Review> reviews = reviewRepository.findByMovieId(movieId);
        return reviews.stream()
                .map(this::convertToReviewDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsForUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("未找到 ID 为 " + userId + " 的用户");
        }
        List<Review> reviews = reviewRepository.findByUserId(userId);
        return reviews.stream()
                .map(this::convertToReviewDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewDTO addReview(Long movieId, Long userId, ReviewRequest reviewRequest) {
        if (reviewRepository.findByMovieIdAndUserId(movieId, userId).isPresent()) {
            throw new IllegalStateException("您已经评论过这部电影了。");
        }

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到 ID 为 " + movieId + " 的电影"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到 ID 为 " + userId + " 的用户"));

        Review review = new Review();
        review.setMovie(movie);
        review.setUser(user);
        review.setCommentText(reviewRequest.getCommentText());
        review.setScore(reviewRequest.getScore());

        reviewRepository.save(review);

        // 评论保存后，调用 MovieService 更新平均分
        movieService.updateMovieAverageRating(movieId);

        return convertToReviewDTO(review);
    }

    @Transactional
    public ReviewDTO updateReview(Long reviewId, ReviewRequest reviewRequest) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到 ID 为 " + reviewId + " 的评论"));

        review.setCommentText(reviewRequest.getCommentText());
        review.setScore(reviewRequest.getScore());

        Review updatedReview = reviewRepository.save(review);

        // 更新后也要重新计算平均分
        movieService.updateMovieAverageRating(review.getMovie().getId());

        return convertToReviewDTO(updatedReview);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到 ID 为 " + reviewId + " 的评论"));

        Long movieId = review.getMovie().getId();

        reviewRepository.delete(review);

        // 删除后也要更新平均分
        movieService.updateMovieAverageRating(movieId);
    }

    @Transactional
    public ReviewDTO voteOnReview(Long reviewId, String direction) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到 ID 为 " + reviewId + " 的评论"));

        if ("up".equalsIgnoreCase(direction)) {
            review.setLikes(review.getLikes() + 1);
        } else if ("down".equalsIgnoreCase(direction)) {
            review.setLikes(review.getLikes() - 1);
        } else {
            throw new IllegalArgumentException("无效的投票方向，必须是 'up' 或 'down'");
        }

        Review savedReview = reviewRepository.save(review);
        return convertToReviewDTO(savedReview);
    }

    private ReviewDTO convertToReviewDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setCommentText(review.getCommentText());
        dto.setScore(review.getScore());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        dto.setMovieId(review.getMovie().getId());
        dto.setMovieTitle(review.getMovie().getTitle());
        dto.setUserId(review.getUser().getId());
        dto.setUsername(review.getUser().getUsername());
        dto.setUserProfileImageUrl(review.getUser().getProfileImageUrl());
        dto.setLikes(review.getLikes());
        return dto;
    }
}