package com.movie_back.backend.consumer;

import com.movie_back.backend.config.RabbitMQConfig;
import com.movie_back.backend.dto.message.RatingUpdateMessage;
import com.movie_back.backend.entity.Movie;
import com.movie_back.backend.entity.Review;
import com.movie_back.backend.repository.MovieRepository;
import com.movie_back.backend.repository.ReviewRepository;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * 评分更新消费者
 * 监听评分更新消息，异步计算电影平均分
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RatingUpdateConsumer {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;

    @RabbitListener(queues = RabbitMQConfig.RATING_UPDATE_QUEUE)
    public void handleRatingUpdate(RatingUpdateMessage message,
                                   Channel channel,
                                   @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            log.info("收到评分更新消息: movieId={}", message.getMovieId());

            // 1. 查询该电影的所有评论
            List<Review> reviews = reviewRepository.findByMovieId(message.getMovieId());

            if (reviews.isEmpty()) {
                log.warn("电影没有评论: movieId={}", message.getMovieId());
                channel.basicAck(tag, false);
                return;
            }

            // 2. 计算平均分
            double averageScore = reviews.stream()
                    .mapToDouble(Review::getScore)
                    .average()
                    .orElse(0.0);

            // 3. 更新电影平均分
            Movie movie = movieRepository.findById(message.getMovieId())
                    .orElseThrow(() -> new RuntimeException("电影不存在"));

            movie.setAverageRating(averageScore);
            movieRepository.save(movie);

            log.info("电影评分更新成功: movieId={}, averageRating={}", message.getMovieId(), averageScore);

            // 4. 确认消息
            channel.basicAck(tag, false);

        } catch (Exception e) {
            log.error("处理评分更新消息失败: movieId={}", message.getMovieId(), e);
            try {
                // 消息处理失败，拒绝并重新入队（可选择不重新入队）
                channel.basicNack(tag, false, false);
            } catch (IOException ioException) {
                log.error("拒绝消息失败", ioException);
            }
        }
    }
}
