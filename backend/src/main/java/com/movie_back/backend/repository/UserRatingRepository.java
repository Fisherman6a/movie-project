package com.movie_back.backend.repository;

import com.movie_back.backend.dto.movie.RatingDistributionDTO;
import com.movie_back.backend.entity.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRatingRepository extends JpaRepository<UserRating, Long> {

    /**
     * 查找特定用户对特定电影的评分。
     * 这对于检查用户是否已对某电影评分很有用。
     * 
     * @param movieId 电影的 ID。
     * @param userId  用户的 ID。
     * @return 一个包含 UserRating 的 Optional 对象（如果存在）。
     */
    Optional<UserRating> findByMovieIdAndUserId(Long movieId, Long userId);

    /**
     * 计算给定电影的平均分。
     * 
     * @param movieId 电影的 ID。
     * @return 平均分；如果没有评分，则返回 null。
     */
    @Query("SELECT AVG(ur.score) FROM UserRating ur WHERE ur.movie.id = :movieId")
    Double getAverageRatingForMovie(Long movieId);

    // 注意：score 是 1-10 分，我们将其除以2并向上取整来映射到 1-5 星
    @Query("SELECT new com.movie_back.backend.dto.movie.RatingDistributionDTO(CAST(CEIL(ur.score / 2.0) AS int), COUNT(ur.id), 0.0) "
            +
            "FROM UserRating ur WHERE ur.movie.id = :movieId GROUP BY CEIL(ur.score / 2.0)")
    List<RatingDistributionDTO> getRatingDistributionForMovie(Long movieId);
}