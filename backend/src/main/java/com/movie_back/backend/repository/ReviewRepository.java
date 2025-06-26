package com.movie_back.backend.repository;

import com.movie_back.backend.dto.movie.RatingDistributionDTO;
import com.movie_back.backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure; // 引入 Procedure
import org.springframework.data.repository.query.Param; // 引入 Param
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

    @Query("SELECT new com.movie_back.backend.dto.movie.RatingDistributionDTO(CAST(CEIL(r.score / 2.0) AS int), COUNT(r.id), 0.0) "
            +
            "FROM Review r WHERE r.movie.id = :movieId GROUP BY 1")
    List<RatingDistributionDTO> getRatingDistributionForMovie(Long movieId);

    // 添加调用存储过程的方法
    @Procedure(name = "get_reviews_by_movie_title")
    List<Review> findReviewsByMovieTitleProcedure(@Param("movieTitle") String movieTitle);
}