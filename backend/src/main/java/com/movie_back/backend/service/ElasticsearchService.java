package com.movie_back.backend.service;

import com.movie_back.backend.document.MovieDocument;
import com.movie_back.backend.entity.Actor;
import com.movie_back.backend.entity.Director;
import com.movie_back.backend.entity.Movie;
import com.movie_back.backend.repository.MovieRepository;
import com.movie_back.backend.repository.MovieSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Elasticsearch 搜索服务
 * 提供基于 ES 的全文搜索和综合查询功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchService {

    private final MovieSearchRepository movieSearchRepository;
    private final MovieRepository movieRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * 将 MySQL 的 Movie 实体同步到 ES
     */
    @Transactional(readOnly = true)
    public void indexMovie(Long movieId) {
        try {
            Movie movie = movieRepository.findById(movieId).orElse(null);
            if (movie == null) {
                log.warn("Movie not found for indexing: id={}", movieId);
                return;
            }

            // 强制加载懒加载关联
            movie.getCast().size();
            movie.getDirectors().size();

            MovieDocument document = convertToDocument(movie);
            movieSearchRepository.save(document);
            log.info("Successfully indexed movie: id={}, title={}", movieId, movie.getTitle());
        } catch (Exception e) {
            log.error("Failed to index movie: id={}", movieId, e);
            throw new RuntimeException("Failed to index movie", e);
        }
    }

    /**
     * 从 ES 中删除电影索引
     */
    public void deleteMovieIndex(Long movieId) {
        try {
            movieSearchRepository.deleteById(movieId);
            log.info("Successfully deleted movie index: id={}", movieId);
        } catch (Exception e) {
            log.error("Failed to delete movie index: id={}", movieId, e);
        }
    }

    /**
     * 综合搜索 - 搜索标题、演员、导演、简介
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页数量
     * @return 搜索结果列表
     */
    public List<MovieDocument> comprehensiveSearch(String keyword, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "averageRating"));

            // 使用 Criteria 构建复合查询
            Criteria criteria = new Criteria("title").contains(keyword)
                    .or("actorNames").contains(keyword)
                    .or("directorNames").contains(keyword)
                    .or("synopsis").contains(keyword)
                    .or("genre").contains(keyword);

            CriteriaQuery query = new CriteriaQuery(criteria).setPageable(pageable);
            SearchHits<MovieDocument> searchHits = elasticsearchOperations.search(query, MovieDocument.class);

            return searchHits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Comprehensive search failed for keyword: {}", keyword, e);
            return List.of();
        }
    }

    /**
     * 根据标题搜索
     */
    public List<MovieDocument> searchByTitle(String title) {
        try {
            return movieSearchRepository.findByTitleContaining(title);
        } catch (Exception e) {
            log.error("Search by title failed: {}", title, e);
            return List.of();
        }
    }

    /**
     * 根据演员名搜索
     */
    public List<MovieDocument> searchByActor(String actorName) {
        try {
            return movieSearchRepository.findByActorNamesContaining(actorName);
        } catch (Exception e) {
            log.error("Search by actor failed: {}", actorName, e);
            return List.of();
        }
    }

    /**
     * 根据导演名搜索
     */
    public List<MovieDocument> searchByDirector(String directorName) {
        try {
            return movieSearchRepository.findByDirectorNamesContaining(directorName);
        } catch (Exception e) {
            log.error("Search by director failed: {}", directorName, e);
            return List.of();
        }
    }

    /**
     * 高级搜索 - 支持多条件过滤
     */
    public List<MovieDocument> advancedSearch(
            String keyword,
            String genre,
            String country,
            Double minRating,
            Integer yearStart,
            Integer yearEnd,
            int page,
            int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "averageRating"));

            Criteria criteria = new Criteria();

            // 关键词搜索（标题、演员、导演、简介）
            if (keyword != null && !keyword.isEmpty()) {
                criteria = criteria.and(
                        new Criteria("title").contains(keyword)
                                .or("actorNames").contains(keyword)
                                .or("directorNames").contains(keyword)
                                .or("synopsis").contains(keyword)
                );
            }

            // 类型过滤
            if (genre != null && !genre.isEmpty()) {
                criteria = criteria.and(new Criteria("genre").contains(genre));
            }

            // 国家过滤
            if (country != null && !country.isEmpty()) {
                criteria = criteria.and(new Criteria("country").is(country));
            }

            // 最低评分过滤
            if (minRating != null) {
                criteria = criteria.and(new Criteria("averageRating").greaterThanEqual(minRating));
            }

            // 年份范围过滤
            if (yearStart != null) {
                criteria = criteria.and(new Criteria("releaseYear").greaterThanEqual(yearStart));
            }
            if (yearEnd != null) {
                criteria = criteria.and(new Criteria("releaseYear").lessThanEqual(yearEnd));
            }

            CriteriaQuery query = new CriteriaQuery(criteria).setPageable(pageable);
            SearchHits<MovieDocument> searchHits = elasticsearchOperations.search(query, MovieDocument.class);

            return searchHits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Advanced search failed", e);
            return List.of();
        }
    }

    /**
     * 将 Movie 实体转换为 MovieDocument
     */
    private MovieDocument convertToDocument(Movie movie) {
        List<String> actorNames = movie.getCast().stream()
                .map(Actor::getName)
                .collect(Collectors.toList());

        List<String> directorNames = movie.getDirectors().stream()
                .map(Director::getName)
                .collect(Collectors.toList());

        return MovieDocument.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .releaseYear(movie.getReleaseYear())
                .duration(movie.getDuration())
                .genre(movie.getGenre())
                .language(movie.getLanguage())
                .country(movie.getCountry())
                .synopsis(movie.getSynopsis())
                .averageRating(movie.getAverageRating())
                .posterUrl(movie.getPosterUrl())
                .actorNames(actorNames)
                .directorNames(directorNames)
                .updateTimestamp(System.currentTimeMillis())
                .build();
    }
}
