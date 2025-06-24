package com.movie_back.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.movie_back.backend.entity.Review;

import java.util.List;
import java.util.Optional; // 确保导入 Optional

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMovieId(Long movieId);

    List<Review> findByUserId(Long userId);

    // **核心修改 2**: 新增方法，用于检查特定用户对特定电影是否已有评论
    Optional<Review> findByMovieIdAndUserId(Long movieId, Long userId);
}