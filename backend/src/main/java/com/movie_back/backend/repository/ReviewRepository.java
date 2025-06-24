package com.movie_back.backend.repository;

import com.movie_back.backend.dto.movie.RatingDistributionDTO;
import com.movie_back.backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMovieId(Long movieId);

    List<Review> findByUserId(Long userId);

    Optional<Review> findByMovieIdAndUserId(Long movieId, Long userId);

    @Query("SELECT AVG(r.score) FROM Review r WHERE r.movie.id = :movieId")
    Double getAverageRatingForMovie(Long movieId);

    // **核心修正**: 添加评分分布的查询
    // 注意：score 是 1-10 分，我们将其除以2并向上取整来映射到 1-5 星
    @Query("SELECT new com.movie_back.backend.dto.movie.RatingDistributionDTO(CAST(CEIL(r.score / 2.0) AS int), COUNT(r.id), 0.0) "
            +
            "FROM Review r WHERE r.movie.id = :movieId GROUP BY 1")
    List<RatingDistributionDTO> getRatingDistributionForMovie(Long movieId);
}