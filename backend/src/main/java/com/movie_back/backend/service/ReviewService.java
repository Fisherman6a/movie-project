package com.movie_back.backend.service;

import com.movie_back.backend.dto.review.ReviewDTO;
import com.movie_back.backend.dto.review.ReviewRequest;
import com.movie_back.backend.entity.Movie;
import com.movie_back.backend.entity.Review;
import com.movie_back.backend.entity.ReviewLike;
import com.movie_back.backend.entity.User;
import com.movie_back.backend.entity.VReviewDetail;
import com.movie_back.backend.exception.ResourceNotFoundException;
import com.movie_back.backend.repository.MovieRepository;
import com.movie_back.backend.repository.ReviewLikeRepository;
import com.movie_back.backend.repository.ReviewRepository;
import com.movie_back.backend.repository.UserRepository;
import com.movie_back.backend.repository.VReviewDetailRepository;

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
    private final VReviewDetailRepository vReviewDetailRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    public ReviewService(ReviewRepository reviewRepository, MovieRepository movieRepository,
            UserRepository userRepository, VReviewDetailRepository vReviewDetailRepository,
            ReviewLikeRepository reviewLikeRepository) {
        this.reviewRepository = reviewRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.vReviewDetailRepository = vReviewDetailRepository;
        this.reviewLikeRepository = reviewLikeRepository;
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getAllReviews() {
        // 使用新的 Repository 查询视图
        return vReviewDetailRepository.findAll().stream()
                // 使用新的转换方法
                .map(this::convertVReviewToDTO)
                .sorted(Comparator.comparing(ReviewDTO::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReviewDTO getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到 ID 为 " + reviewId + " 的评论"));
        return convertToReviewDTO(review);
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

        Review savedReview = reviewRepository.save(review);
        return convertToReviewDTO(savedReview);
    }

    @Transactional
    public ReviewDTO updateReview(Long reviewId, ReviewRequest reviewRequest) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到 ID 为 " + reviewId + " 的评论"));
        review.setCommentText(reviewRequest.getCommentText());
        review.setScore(reviewRequest.getScore());

        Review updatedReview = reviewRepository.save(review);
        return convertToReviewDTO(updatedReview);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("未找到 ID 为 " + reviewId + " 的评论");
        }

        // 先删除所有关联的点赞记录（避免外键约束）
        reviewLikeRepository.deleteByReviewId(reviewId);

        // 再删除评论
        reviewRepository.deleteById(reviewId);
    }

    /**
     * 点赞/取消点赞评论
     * @param reviewId 评论ID
     * @param userId 点赞用户ID
     * @param direction "up" 点赞，"down" 取消点赞
     * @return 更新后的评论DTO
     */
    @Transactional
    public ReviewDTO voteOnReview(Long reviewId, Long userId, String direction) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到 ID 为 " + reviewId + " 的评论"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到 ID 为 " + userId + " 的用户"));

        if ("up".equalsIgnoreCase(direction)) {
            // 点赞逻辑
            boolean alreadyLiked = reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId);

            if (alreadyLiked) {
                throw new IllegalStateException("您已经点过赞了");
            }

            // 创建点赞记录
            ReviewLike reviewLike = ReviewLike.builder()
                    .review(review)
                    .user(user)
                    .build();
            reviewLikeRepository.save(reviewLike);

            // 增加点赞数
            review.setLikes(review.getLikes() + 1);

        } else if ("down".equalsIgnoreCase(direction)) {
            // 取消点赞逻辑
            boolean hasLiked = reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId);

            if (!hasLiked) {
                throw new IllegalStateException("您还没有点过赞");
            }

            // 删除点赞记录
            reviewLikeRepository.deleteByReviewIdAndUserId(reviewId, userId);

            // 减少点赞数
            review.setLikes(Math.max(0, review.getLikes() - 1)); // 防止负数

        } else {
            throw new IllegalArgumentException("无效的投票方向，必须是 'up' 或 'down'");
        }

        Review savedReview = reviewRepository.save(review);
        return convertToReviewDTO(savedReview);
    }

    /**
     * 检查用户是否已点赞某条评论
     */
    @Transactional(readOnly = true)
    public boolean hasUserLikedReview(Long reviewId, Long userId) {
        return reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId);
    }

    // 新增调用存储过程的服务方法
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByMovieTitleFromProcedure(String movieTitle) {
        List<Review> reviews = reviewRepository.findReviewsByMovieTitleProcedure(movieTitle);
        return reviews.stream()
                .map(this::convertToReviewDTO)
                .collect(Collectors.toList());
    }

    // 该转换方法用于处理来自视图的数据
    private ReviewDTO convertVReviewToDTO(VReviewDetail vReview) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(vReview.getReviewId());
        dto.setCommentText(vReview.getCommentText());
        dto.setScore(vReview.getScore());
        dto.setLikes(vReview.getLikes());
        dto.setCreatedAt(vReview.getCreatedAt());
        dto.setMovieId(vReview.getMovieId());
        dto.setMovieTitle(vReview.getMovieTitle());
        dto.setUserId(vReview.getUserId());
        dto.setUsername(vReview.getUsername());
        return dto;
    }

    private ReviewDTO convertToReviewDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setCommentText(review.getCommentText());
        dto.setScore(review.getScore());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setLikes(review.getLikes());

        // 为了保证在不同调用场景下都能正确填充信息
        if (review.getMovie() != null) {
            dto.setMovieId(review.getMovie().getId());
            dto.setMovieTitle(review.getMovie().getTitle());
        }
        if (review.getUser() != null) {
            dto.setUserId(review.getUser().getId());
            dto.setUsername(review.getUser().getUsername());
            dto.setUserProfileImageUrl(review.getUser().getProfileImageUrl());
        }
        return dto;
    }
}
