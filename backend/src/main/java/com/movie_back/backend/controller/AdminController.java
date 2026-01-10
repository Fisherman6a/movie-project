package com.movie_back.backend.controller;

import com.movie_back.backend.entity.Movie;
import com.movie_back.backend.repository.MovieRepository;
import com.movie_back.backend.service.ElasticsearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 * 提供系统管理相关的接口
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ElasticsearchService elasticsearchService;
    private final MovieRepository movieRepository;

    /**
     * 重建 Elasticsearch 索引
     * POST /api/admin/es/rebuild-index
     * 需要管理员权限
     */
    @PostMapping("/es/rebuild-index")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> rebuildElasticsearchIndex() {
        log.info("开始重建 Elasticsearch 索引...");
        Map<String, Object> response = new HashMap<>();

        try {
            List<Movie> allMovies = movieRepository.findAll();
            log.info("找到 {} 部电影,开始重建索引...", allMovies.size());

            int successCount = 0;
            int failCount = 0;

            for (Movie movie : allMovies) {
                try {
                    // 强制加载懒加载关联
                    movie.getCast().size();
                    movie.getDirectors().size();

                    elasticsearchService.indexMovie(movie.getId());
                    successCount++;

                    if (successCount % 10 == 0) {
                        log.info("已索引 {}/{} 部电影...", successCount, allMovies.size());
                    }
                } catch (Exception e) {
                    failCount++;
                    log.error("索引电影失败: id={}, title={}", movie.getId(), movie.getTitle(), e);
                }
            }

            log.info("Elasticsearch 索引重建完成! 成功: {}, 失败: {}", successCount, failCount);

            response.put("status", "success");
            response.put("message", "索引重建完成");
            response.put("totalMovies", allMovies.size());
            response.put("successCount", successCount);
            response.put("failCount", failCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("重建 Elasticsearch 索引失败", e);
            response.put("status", "error");
            response.put("message", "索引重建失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 索引单个电影
     * POST /api/admin/es/index/{movieId}
     */
    @PostMapping("/es/index/{movieId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> indexSingleMovie(@PathVariable Long movieId) {
        Map<String, String> response = new HashMap<>();
        try {
            elasticsearchService.indexMovie(movieId);
            response.put("status", "success");
            response.put("message", "电影索引成功");
            response.put("movieId", movieId.toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("索引电影失败: movieId={}", movieId, e);
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 删除单个电影索引
     * DELETE /api/admin/es/index/{movieId}
     */
    @DeleteMapping("/es/index/{movieId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteMovieIndex(@PathVariable Long movieId) {
        Map<String, String> response = new HashMap<>();
        try {
            elasticsearchService.deleteMovieIndex(movieId);
            response.put("status", "success");
            response.put("message", "电影索引删除成功");
            response.put("movieId", movieId.toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("删除电影索引失败: movieId={}", movieId, e);
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
