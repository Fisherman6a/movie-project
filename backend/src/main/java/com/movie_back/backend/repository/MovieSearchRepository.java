package com.movie_back.backend.repository;

import com.movie_back.backend.document.MovieDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Elasticsearch 电影搜索仓库
 */
@Repository
public interface MovieSearchRepository extends ElasticsearchRepository<MovieDocument, Long> {

    /**
     * 根据标题搜索（模糊匹配）
     */
    List<MovieDocument> findByTitleContaining(String title);

    /**
     * 根据演员名搜索
     */
    List<MovieDocument> findByActorNamesContaining(String actorName);

    /**
     * 根据导演名搜索
     */
    List<MovieDocument> findByDirectorNamesContaining(String directorName);

    /**
     * 根据类型搜索
     */
    List<MovieDocument> findByGenreContaining(String genre);

    /**
     * 根据国家搜索
     */
    List<MovieDocument> findByCountry(String country);

    /**
     * 根据评分范围搜索
     */
    List<MovieDocument> findByAverageRatingGreaterThanEqual(Double minRating);
}
