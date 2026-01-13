package com.movie_back.backend.repository;

import com.movie_back.backend.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    /**
     * 检查用户是否已点赞某条评论
     */
    boolean existsByReviewIdAndUserId(Long reviewId, Long userId);

    /**
     * 查找用户对某条评论的点赞记录
     */
    Optional<ReviewLike> findByReviewIdAndUserId(Long reviewId, Long userId);

    /**
     * 删除用户对某条评论的点赞记录（取消点赞）
     */
    void deleteByReviewIdAndUserId(Long reviewId, Long userId);

    /**
     * 删除某条评论的所有点赞记录
     */
    void deleteByReviewId(Long reviewId);

    /**
     * 删除某个用户的所有点赞记录（用于删除用户时清理）
     */
    void deleteByUserId(Long userId);

    /**
     * 批量查询用户对多条评论的点赞状态
     * 用于评论列表展示时，一次性查出当前用户点赞了哪些评论
     */
    @Query("SELECT rl.review.id FROM ReviewLike rl WHERE rl.review.id IN :reviewIds AND rl.user.id = :userId")
    Set<Long> findLikedReviewIdsByUserIdAndReviewIds(@Param("userId") Long userId, @Param("reviewIds") List<Long> reviewIds);

    /**
     * 统计某条评论的点赞数（从关系表统计，用于验证）
     */
    long countByReviewId(Long reviewId);
}
