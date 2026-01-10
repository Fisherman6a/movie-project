# 点赞通知功能测试指南

## 问题现象
点赞评论后，评论作者没有收到弹窗通知

## 排查步骤

### 1. 检查前端 WebSocket 连接状态

**操作**：
1. 登录一个用户账号
2. 打开浏览器开发者工具（F12）
3. 查看 Console 控制台

**预期日志**：
```
用户已登录，建立 WebSocket 连接, userId: xxx
STOMP Debug: ...
WebSocket 连接成功: ...
```

**如果看到连接失败**：
- 检查后端 WebSocket 服务是否启动（端口7070）
- 检查 `websocketService.js` 中的连接地址是否正确

---

### 2. 测试点赞流程

**操作**：
1. 用户A登录，发表一条评论
2. 用户B登录，给用户A的评论点赞
3. 查看用户A的浏览器控制台

**预期日志（用户B点赞时）**：
```
发送点赞请求...（前端）
```

**预期后端日志**：
```
发送点赞通知消息: reviewId=xxx, authorId=xxx
收到点赞通知消息: reviewId=xxx, authorId=xxx
通知已保存到数据库: notificationId=xxx
发送WebSocket通知: userId=xxx, type=LIKE
点赞通知推送成功: authorId=xxx
```

**预期日志（用户A收到通知）**：
```
收到通知: {type: "LIKE", title: "评论点赞", message: "用户 xxx 赞了你对《xxx》的评论", ...}
处理通知: {type: "LIKE", ...}
```

**前端应该弹出**：
- 右上角蓝色消息提示
- 内容："用户 XXX 赞了你对《电影名》的评论"
- 5秒后自动消失

---

### 3. 常见问题

#### 问题1：WebSocket 连接失败
**可能原因**：
- 后端未启动
- 端口被占用
- CORS 配置问题

**解决方法**：
检查 `WebSocketConfig.java` 中的 `setAllowedOrigins` 是否包含前端地址

#### 问题2：后端没有发送消息到 RabbitMQ
**检查**：
- 后端日志中是否有 "发送点赞通知消息"
- RabbitMQ 管理界面（http://localhost:15672）查看队列是否有消息

#### 问题3：RabbitMQ 消费者未消费
**检查**：
- 后端日志中是否有 "收到点赞通知消息"
- RabbitMQ 队列中消息是否积压

#### 问题4：WebSocket 推送失败
**检查**：
- 用户A是否已登录且WebSocket已连接
- 后端日志中是否有 "发送WebSocket通知失败"

#### 问题5：前端未收到或未显示
**检查**：
- 浏览器控制台是否有 "收到通知" 日志
- `NotificationHandler.vue` 是否正确导入到 `App.vue`
- `useMessage` 是否在 `n-message-provider` 内部调用

---

## 快速测试命令

### 检查 RabbitMQ 队列状态
```bash
# 进入 RabbitMQ 容器
docker exec -it rabbitmq-dev rabbitmqctl list_queues

# 或访问管理界面
http://localhost:15672
用户名: guest
密码: guest
```

### 检查 WebSocket 连接
打开浏览器开发者工具 → Network → WS (WebSocket) → 查看连接状态

---

## 调试建议

1. **开启详细日志**：
   - 临时取消注释 `application.properties` 中的 DEBUG 日志配置

2. **测试步骤**：
   - 先用两个浏览器窗口（或无痕模式）登录不同用户
   - 确保两个用户的 WebSocket 都已连接
   - 然后测试点赞

3. **检查数据库**：
   ```sql
   -- 查看通知是否保存到数据库
   SELECT * FROM notifications ORDER BY created_at DESC LIMIT 10;
   ```

4. **手动触发测试**：
   - 访问 `http://localhost:7070/test/send-notification?userId=xxx`
   - 看是否能收到测试通知
