# RabbitMQ + WebSocket 异步消息系统实现文档

## 📋 业务需求

1. **异步更新电影评分**：用户发表评论后立即返回,后台异步计算平均分
2. **评论点赞通知**：评论被点赞通知作者(WebSocket站内信)
3. **搜索索引更新**：电影数据变更后异步更新Elasticsearch
4. **日志收集**：用户行为日志异步入库

---

## 🏗️ 系统架构

```
[用户操作] → [Controller] → [发送MQ消息] → [RabbitMQ]
                                                    ↓
                    [消费者1: 评分计算] ← [队列: rating.update]
                    [消费者2: 点赞通知] ← [队列: like.notification] → [WebSocket推送]
                    [消费者3: ES索引] ← [队列: index.update]
                    [消费者4: 日志入库] ← [队列: log.collect]
```

---

## 📦 已完成的配置

### 1. 依赖添加 (`pom.xml`)
- ✅ spring-boot-starter-amqp (RabbitMQ)
- ✅ spring-boot-starter-websocket (WebSocket)
- ✅ spring-boot-starter-data-elasticsearch (Elasticsearch)

### 2. 配置文件 (`application.properties`)
```properties
# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

# Elasticsearch
spring.elasticsearch.uris=http://localhost:9200
```

---

## 🛠️ 实现步骤

### 步骤1: RabbitMQ 配置

创建文件: `backend/src/main/java/com/movie_back/backend/config/RabbitMQConfig.java`

```java
@Configuration
public class RabbitMQConfig {
    // 定义队列名称
    public static final String RATING_UPDATE_QUEUE = "rating.update";
    public static final String LIKE_NOTIFICATION_QUEUE = "like.notification";
    public static final String INDEX_UPDATE_QUEUE = "index.update";
    public static final String LOG_COLLECT_QUEUE = "log.collect";

    // 定义交换机
    public static final String MOVIE_EXCHANGE = "movie.exchange";

    // 定义路由键
    public static final String RATING_UPDATE_ROUTING_KEY = "rating.update";
    public static final String LIKE_NOTIFICATION_ROUTING_KEY = "like.notification";
    public static final String INDEX_UPDATE_ROUTING_KEY = "index.update";
    public static final String LOG_COLLECT_ROUTING_KEY = "log.collect";

    // 创建队列、交换机、绑定...
}
```

### 步骤2: WebSocket 配置

创建文件: `backend/src/main/java/com/movie_back/backend/config/WebSocketConfig.java`

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:8080", "http://localhost:8081")
                .withSockJS();
    }
}
```

### 步骤3: 消息模型

创建文件: `backend/src/main/java/com/movie_back/backend/dto/message/`

- `RatingUpdateMessage.java` - 评分更新消息
- `LikeNotificationMessage.java` - 点赞通知消息
- `IndexUpdateMessage.java` - 索引更新消息
- `UserActionLog.java` - 用户行为日志

### 步骤4: 消息生产者服务

创建文件: `backend/src/main/java/com/movie_back/backend/service/MessageProducerService.java`

```java
@Service
public class MessageProducerService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendRatingUpdateMessage(Long movieId) {
        // 发送评分更新消息到 RabbitMQ
    }

    public void sendLikeNotification(Long reviewId, Long authorId) {
        // 发送点赞通知消息
    }

    public void sendIndexUpdateMessage(Long movieId, String operation) {
        // 发送索引更新消息
    }

    public void sendUserActionLog(String action, Long userId, Map<String, Object> details) {
        // 发送用户行为日志
    }
}
```

### 步骤5: 消息消费者

创建文件: `backend/src/main/java/com/movie_back/backend/consumer/`

- `RatingUpdateConsumer.java` - 监听 rating.update 队列,计算电影平均分
- `LikeNotificationConsumer.java` - 监听 like.notification 队列,通过 WebSocket 推送通知
- `IndexUpdateConsumer.java` - 监听 index.update 队列,更新 Elasticsearch 索引
- `LogCollectConsumer.java` - 监听 log.collect 队列,日志入库

### 步骤6: WebSocket 通知服务

创建文件: `backend/src/main/java/com/movie_back/backend/service/WebSocketNotificationService.java`

```java
@Service
public class WebSocketNotificationService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNotificationToUser(Long userId, NotificationMessage message) {
        // 向特定用户发送 WebSocket 消息
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/notifications",
            message
        );
    }
}
```

### 步骤7: Elasticsearch 文档模型

创建文件: `backend/src/main/java/com/movie_back/backend/document/MovieDocument.java`

```java
@Document(indexName = "movies")
public class MovieDocument {
    @Id
    private Long id;
    private String title;
    private String description;
    private Double averageScore;
    private List<String> actorNames;
    private String directorName;
    // ...
}
```

### 步骤8: 修改现有 Controller

修改: `ReviewController.java`

```java
// 添加评论后
@PostMapping("/{movieId}/reviews")
public ResponseEntity<?> addReview(...) {
    // 1. 保存评论到数据库
    Review saved = reviewService.save(review);

    // 2. 发送 MQ 消息 - 异步更新评分
    messageProducer.sendRatingUpdateMessage(movieId);

    // 3. 发送用户行为日志
    messageProducer.sendUserActionLog("ADD_REVIEW", userId, details);

    // 4. 立即返回
    return ResponseEntity.ok(saved);
}

// 点赞评论
@PostMapping("/reviews/{reviewId}/vote")
public ResponseEntity<?> voteOnReview(...) {
    // 1. 保存点赞记录
    voteService.save(vote);

    // 2. 发送点赞通知消息
    Long authorId = review.getUser().getId();
    messageProducer.sendLikeNotification(reviewId, authorId);

    return ResponseEntity.ok("点赞成功");
}
```

修改: `MovieController.java`

```java
// 更新电影后
@PutMapping("/{id}")
public ResponseEntity<?> updateMovie(...) {
    // 1. 更新数据库
    Movie updated = movieService.update(movie);

    // 2. 发送 ES 索引更新消息
    messageProducer.sendIndexUpdateMessage(id, "UPDATE");

    return ResponseEntity.ok(updated);
}
```

### 步骤9: 前端 WebSocket 集成

创建文件: `frontend/src/services/websocketService.js`

```javascript
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

class WebSocketService {
    constructor() {
        this.stompClient = null;
        this.connected = false;
    }

    connect(userId, onNotification) {
        const socket = new SockJS('http://localhost:7070/ws');
        this.stompClient = new Client({
            webSocketFactory: () => socket,
            onConnect: () => {
                console.log('WebSocket 连接成功');
                this.connected = true;

                // 订阅用户专属通知队列
                this.stompClient.subscribe(
                    `/queue/${userId}/notifications`,
                    (message) => {
                        const notification = JSON.parse(message.body);
                        onNotification(notification);
                    }
                );
            },
            onStompError: (error) => {
                console.error('WebSocket 错误:', error);
            }
        });

        this.stompClient.activate();
    }

    disconnect() {
        if (this.stompClient) {
            this.stompClient.deactivate();
            this.connected = false;
        }
    }
}

export default new WebSocketService();
```

### 步骤10: 前端通知组件

创建文件: `frontend/src/components/NotificationToast.vue`

```vue
<template>
  <n-notification-provider>
    <n-message-provider>
      <!-- 应用内容 -->
    </n-message-provider>
  </n-notification-provider>
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue';
import { useMessage } from 'naive-ui';
import websocketService from '@/services/websocketService';
import { useAuthStore } from '@/stores/authStore';

const message = useMessage();
const authStore = useAuthStore();

onMounted(() => {
    if (authStore.isAuthenticated) {
        // 连接 WebSocket
        websocketService.connect(authStore.userId, (notification) => {
            // 显示通知
            message.success(notification.message, {
                duration: 5000
            });
        });
    }
});

onUnmounted(() => {
    websocketService.disconnect();
});
</script>
```

---

## 🧪 测试流程

### 1. 测试评分异步计算

```bash
# 1. 发表评论
POST /api/movies/1/reviews
{
    "userId": 1,
    "score": 10,
    "commentText": "非常好看!"
}

# 2. 立即返回 (不等待评分计算)
# 3. 后台 RabbitMQ 消费者自动计算平均分
# 4. 查看电影详情,评分已更新
GET /api/movies/1
```

### 2. 测试点赞通知

```bash
# 1. 用户A对用户B的评论点赞
POST /api/reviews/123/vote
{
    "direction": "UP"
}

# 2. 用户B的浏览器立即收到 WebSocket 通知:
# "您的评论《肖申克的救赎》被点赞了!"
```

### 3. 测试 ES 索引更新

```bash
# 1. 更新电影信息
PUT /api/movies/1
{
    "title": "肖申克的救赎 (修订版)"
}

# 2. 后台自动更新 Elasticsearch 索引
# 3. 搜索时能找到新标题
GET /api/movies/search?q=修订版
```

### 4. 测试日志收集

```bash
# 用户的所有操作都会异步记录到日志表:
# - 登录
# - 浏览电影
# - 发表评论
# - 点赞
# ...
```

---

## ⚠️ 注意事项

1. **消息幂等性**: 消费者需要处理重复消息的情况
2. **消息丢失**: 使用消息确认机制 (manual ACK)
3. **死信队列**: 处理失败的消息
4. **WebSocket 鉴权**: 确保只有登录用户能连接
5. **性能考虑**: 大量消息时考虑消费者并发数

---

## 📊 数据库表设计

### 通知表 (notifications)

```sql
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '接收通知的用户ID',
    type VARCHAR(50) NOT NULL COMMENT '通知类型: LIKE, COMMENT, SYSTEM',
    title VARCHAR(255) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    related_id BIGINT COMMENT '关联ID(评论ID/电影ID等)',
    is_read BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 用户行为日志表 (user_action_logs)

```sql
CREATE TABLE user_action_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '用户ID',
    action VARCHAR(50) NOT NULL COMMENT '操作类型: VIEW, SEARCH, REVIEW, LIKE',
    entity_type VARCHAR(50) COMMENT '实体类型: MOVIE, REVIEW',
    entity_id BIGINT COMMENT '实体ID',
    details JSON COMMENT '详细信息',
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 🎯 下一步

请告诉我你想先实现哪个功能:

1. ✅ **评分异步计算** (最核心)
2. 🔔 **点赞通知 + WebSocket** (最有趣)
3. 🔍 **Elasticsearch 索引更新**
4. 📊 **用户行为日志收集**

我会为你选择的功能创建完整的代码!
