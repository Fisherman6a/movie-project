package com.movie_back.backend.consumer;

import com.movie_back.backend.config.RabbitMQConfig;
import com.movie_back.backend.dto.message.IndexUpdateMessage;
import com.movie_back.backend.service.ElasticsearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Elasticsearch 索引更新消费者
 * 监听索引更新消息,异步更新 ES 索引
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IndexUpdateConsumer {

    private final ElasticsearchService elasticsearchService;

    @RabbitListener(queues = RabbitMQConfig.INDEX_UPDATE_QUEUE)
    public void handleIndexUpdate(IndexUpdateMessage message) {
        try {
            log.info("收到索引更新消息: movieId={}, operation={}", message.getMovieId(), message.getOperation());

            switch (message.getOperation()) {
                case "CREATE":
                case "UPDATE":
                    // 创建或更新索引
                    elasticsearchService.indexMovie(message.getMovieId());
                    log.info("索引更新成功: movieId={}, operation={}", message.getMovieId(), message.getOperation());
                    break;

                case "DELETE":
                    // 删除索引
                    elasticsearchService.deleteMovieIndex(message.getMovieId());
                    log.info("索引删除成功: movieId={}", message.getMovieId());
                    break;

                default:
                    log.warn("未知的操作类型: {}", message.getOperation());
            }

        } catch (Exception e) {
            log.error("处理索引更新消息失败: movieId={}, operation={}",
                    message.getMovieId(), message.getOperation(), e);
            // 使用自动ACK,异常会导致消息重新入队
            throw new RuntimeException("处理索引更新失败", e);
        }
    }
}
