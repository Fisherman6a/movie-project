package com.movie_back.backend.controller;

import com.movie_back.backend.document.MovieDocument;
import com.movie_back.backend.service.ElasticsearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Elasticsearch 搜索控制器
 * 提供基于 ES 的全文搜索和综合查询接口
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final ElasticsearchService elasticsearchService;

    /**
     * 综合搜索 - 搜索电影标题、演员、导演、简介、类型
     * GET /api/search/comprehensive?keyword=肖申克&page=0&size=10
     */
    @GetMapping("/comprehensive")
    public ResponseEntity<List<MovieDocument>> comprehensiveSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<MovieDocument> results = elasticsearchService.comprehensiveSearch(keyword, page, size);
        return ResponseEntity.ok(results);
    }

    /**
     * 高级搜索 - 支持多条件过滤
     * GET /api/search/advanced?keyword=肖申克&genre=剧情&minRating=8.0&yearStart=1990&yearEnd=2000
     */
    @GetMapping("/advanced")
    public ResponseEntity<List<MovieDocument>> advancedSearch(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer yearStart,
            @RequestParam(required = false) Integer yearEnd,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<MovieDocument> results = elasticsearchService.advancedSearch(
                keyword, genre, country, minRating, yearStart, yearEnd, page, size
        );
        return ResponseEntity.ok(results);
    }

    /**
     * 根据标题搜索
     * GET /api/search/by-title?title=肖申克
     */
    @GetMapping("/by-title")
    public ResponseEntity<List<MovieDocument>> searchByTitle(@RequestParam String title) {
        List<MovieDocument> results = elasticsearchService.searchByTitle(title);
        return ResponseEntity.ok(results);
    }

    /**
     * 根据演员名搜索
     * GET /api/search/by-actor?name=蒂姆·罗宾斯
     */
    @GetMapping("/by-actor")
    public ResponseEntity<List<MovieDocument>> searchByActor(@RequestParam String name) {
        List<MovieDocument> results = elasticsearchService.searchByActor(name);
        return ResponseEntity.ok(results);
    }

    /**
     * 根据导演名搜索
     * GET /api/search/by-director?name=弗兰克·德拉邦特
     */
    @GetMapping("/by-director")
    public ResponseEntity<List<MovieDocument>> searchByDirector(@RequestParam String name) {
        List<MovieDocument> results = elasticsearchService.searchByDirector(name);
        return ResponseEntity.ok(results);
    }
}
