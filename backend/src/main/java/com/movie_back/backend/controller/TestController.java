package com.movie_back.backend.controller;

import com.movie_back.backend.service.MessageProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试 Controller
 * 用于测试 RabbitMQ 消息发送
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final MessageProducerService messageProducer;

    /**
     * 测试评分更新消息
     * 访问：GET http://localhost:7070/api/test/rating/1
     */
    @GetMapping("/rating/{movieId}")
    public ResponseEntity<Map<String, String>> testRatingUpdate(@PathVariable Long movieId) {
        messageProducer.sendRatingUpdateMessage(movieId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "评分更新消息已发送");
        response.put("movieId", movieId.toString());

        return ResponseEntity.ok(response);
    }

    /**
     * 测试点赞通知消息
     * 访问：GET http://localhost:7070/api/test/like?reviewId=1&authorId=2&likerId=3&likerName=张三&movieId=1&movieTitle=肖申克的救赎
     */
    @GetMapping("/like")
    public ResponseEntity<Map<String, String>> testLikeNotification(
            @RequestParam Long reviewId,
            @RequestParam Long authorId,
            @RequestParam Long likerId,
            @RequestParam String likerName,
            @RequestParam Long movieId,
            @RequestParam String movieTitle) {

        messageProducer.sendLikeNotification(
            reviewId, authorId, likerId, likerName, movieId, movieTitle
        );

        Map<String, String> response = new HashMap<>();
        response.put("message", "点赞通知消息已发送");
        response.put("reviewId", reviewId.toString());

        return ResponseEntity.ok(response);
    }

    /**
     * 健康检查
     * 访问：GET http://localhost:7070/api/test/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "RabbitMQ 测试接口正常");

        return ResponseEntity.ok(response);
    }
}
