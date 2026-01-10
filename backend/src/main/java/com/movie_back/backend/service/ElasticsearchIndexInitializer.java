package com.movie_back.backend.service;

import com.movie_back.backend.entity.Movie;
import com.movie_back.backend.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Elasticsearch 索引初始化器
 * 应用启动时将 MySQL 中的所有电影数据同步到 ES
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ElasticsearchIndexInitializer {

    private final MovieRepository movieRepository;
    private final ElasticsearchService elasticsearchService;

    /**
     * 应用启动时执行索引初始化
     * 注意: 只在首次启动或需要重建索引时启用
     */
    @Bean
    public CommandLineRunner initElasticsearchIndex() {
        return args -> {
            // 检查是否需要初始化索引
            // 如果不需要自动初始化,可以注释掉下面的代码
            // 或者通过 application.properties 配置开关

            boolean shouldInit = false; // 默认关闭自动初始化

            // 可以通过环境变量或配置文件控制
            String initFlag = System.getenv("ES_INIT_INDEX");
            if ("true".equalsIgnoreCase(initFlag)) {
                shouldInit = true;
            }

            if (!shouldInit) {
                log.info("Elasticsearch 索引自动初始化已禁用");
                log.info("如需手动初始化索引,请访问: POST /api/admin/es/rebuild-index");
                return;
            }

            log.info("开始初始化 Elasticsearch 索引...");
            try {
                List<Movie> allMovies = movieRepository.findAll();
                log.info("找到 {} 部电影,开始索引...", allMovies.size());

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

                log.info("Elasticsearch 索引初始化完成! 成功: {}, 失败: {}", successCount, failCount);
            } catch (Exception e) {
                log.error("Elasticsearch 索引初始化失败", e);
            }
        };
    }
}
