# 🧪 完整测试指南

## 📋 测试前准备

### 1. 确保所有服务已启动

- ✅ **MySQL** (端口 3306)
- ✅ **RabbitMQ** (端口 5672, 管理界面 15672)
- ✅ **后端** (端口 7070)
- ✅ **前端** (端口 8080)

### 2. 启动命令

```bash
# 启动 RabbitMQ (Docker)
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

# 启动后端
cd backend
mvn spring-boot:run

# 启动前端
cd frontend
yarn dev
```

---

## 🎯 测试场景 1: 异步评分计算

### 目标
验证用户发表评论后，系统异步计算电影平均评分并更新数据库。

### 测试步骤

#### 方法 1: 使用测试端点

1. **发送测试请求**
   ```bash
   curl http://localhost:7070/api/test/rating/1
   ```

2. **预期响应**
   ```json
   {
     "message": "评分更新消息已发送",
     "movieId": "1"
   }
   ```

3. **检查后端日志**
   应该看到以下日志：
   ```
   发送评分更新消息: movieId=1
   收到评分更新消息: movieId=1
   电影评分更新成功: movieId=1, averageRating=X.X
   ```

4. **验证 RabbitMQ 管理界面**
   - 访问 http://localhost:15672 (guest/guest)
   - 进入 "Queues" 标签
   - 确认 `rating.update` 队列消息被消费

5. **验证数据库**
   ```sql
   SELECT id, title, average_rating FROM movies WHERE id = 1;
   ```
   检查 `average_rating` 字段是否更新

#### 方法 2: 通过前端发表评论

1. **打开前端** http://localhost:8080

2. **登录用户**
   - 如果没有账号，先注册一个

3. **发表评论**
   - 进入任意电影详情页
   - 填写评论内容和评分
   - 点击提交

4. **观察**
   - 前端应该立即显示评论成功
   - 后端日志应该显示：
     ```
     发送评分更新消息: movieId=X
     收到评分更新消息: movieId=X
     电影评分更新成功
     ```

5. **刷新页面**
   - 电影评分应该已经更新

---

## 🎯 测试场景 2: 点赞实时通知

### 目标
验证用户B点赞用户A的评论后，用户A实时收到WebSocket通知。

### 准备工作

需要两个用户账号：
- **用户A**: 评论作者（接收通知）
- **用户B**: 点赞者（触发通知）

### 测试步骤

#### 第一步：用户A发表评论

1. **打开浏览器1**（建议用Chrome）
   - 访问 http://localhost:8080
   - 按 F12 打开开发者工具
   - 切换到 "Console" 标签

2. **登录用户A**
   - 登录后，控制台应该显示：
     ```
     用户已登录，建立 WebSocket 连接, userId: X
     STOMP Debug: ...
     WebSocket 连接成功: ...
     ```

3. **发表评论**
   - 进入某部电影详情页
   - 发表一条评论
   - 记住这条评论的位置

#### 第二步：用户B点赞评论

1. **打开浏览器2**（建议用Firefox或Edge）
   - 访问 http://localhost:8080
   - 按 F12 打开开发者工具

2. **登录用户B**
   - 使用另一个账号登录
   - 控制台同样应该显示 WebSocket 连接成功

3. **找到用户A的评论**
   - 进入同一部电影详情页
   - 找到用户A刚才发表的评论

4. **点赞**
   - 点击评论的"点赞"按钮

#### 第三步：验证通知

1. **切换回浏览器1（用户A）**

2. **应该看到：**
   - ✅ 页面右上角弹出通知消息（Naive UI 通知）
   - ✅ 通知内容：`用户B赞了你对《电影名》的评论`
   - ✅ 控制台显示：
     ```javascript
     收到通知: {
       type: "LIKE",
       title: "评论点赞",
       message: "用户B赞了你对《电影名》的评论",
       relatedId: X,
       relatedType: "REVIEW",
       timestamp: ...
     }
     处理通知: ...
     ```

3. **检查后端日志**
   ```
   收到点赞通知消息: reviewId=X, authorId=Y
   通知已保存到数据库: notificationId=Z
   点赞通知推送成功: authorId=Y
   ```

4. **检查数据库**
   ```sql
   SELECT * FROM notifications ORDER BY created_at DESC LIMIT 1;
   ```

   应该看到：
   ```sql
   id=1
   user_id=10 (用户A的ID)
   type='LIKE'
   title='评论点赞'
   content='用户B赞了你对《电影名》的评论'
   related_id=5 (评论ID)
   related_type='REVIEW'
   trigger_user_id=20 (用户B的ID)
   trigger_username='用户B的用户名'
   is_read=0
   created_at=...
   ```

5. **检查 reviews 表**
   ```sql
   SELECT id, likes FROM reviews WHERE id = X;
   ```

   `likes` 字段应该 +1

---

## 🔍 调试技巧

### 前端调试

#### 1. WebSocket 连接状态
```javascript
// 在浏览器控制台执行
websocketService.isConnected()  // 应该返回 true
```

#### 2. 查看 WebSocket 连接
- 打开开发者工具 (F12)
- 切换到 "Network" 标签
- 过滤 "WS"（WebSocket）
- 应该看到一个到 `ws://localhost:7070/ws` 的连接
- 状态应该是 "101 Switching Protocols"

#### 3. 查看 WebSocket 消息
- 点击 WebSocket 连接
- 切换到 "Messages" 标签
- 应该看到 STOMP 协议的消息帧

#### 4. 常见前端日志
```
# 正常情况
用户已登录，建立 WebSocket 连接, userId: 10
STOMP Debug: Opening Web Socket...
STOMP Debug: Web Socket Opened...
STOMP Debug: >>> CONNECT
STOMP Debug: <<< CONNECTED
WebSocket 连接成功: ...

# 收到通知
收到通知: {type: "LIKE", message: "..."}
处理通知: ...
```

### 后端调试

#### 1. 查看 RabbitMQ 队列状态
- 访问 http://localhost:15672
- 登录 (guest/guest)
- 进入 "Queues" 标签
- 检查各队列的消息数量：
  - `rating.update` - 评分更新队列
  - `like.notification` - 点赞通知队列
  - `index.update` - 索引更新队列
  - `log.collect` - 日志收集队列

#### 2. 后端关键日志
```bash
# 评分更新流程
发送评分更新消息: movieId=1
收到评分更新消息: movieId=1
电影评分更新成功: movieId=1, averageRating=8.5

# 点赞通知流程
发送点赞通知消息: reviewId=5, authorId=10, likerId=20
收到点赞通知消息: reviewId=5, authorId=10
通知已保存到数据库: notificationId=1
点赞通知推送成功: authorId=10
```

#### 3. 查看数据库变化
```sql
-- 查看最新通知
SELECT * FROM notifications ORDER BY created_at DESC LIMIT 5;

-- 查看未读通知
SELECT * FROM notifications WHERE is_read = 0;

-- 查看某用户的通知
SELECT * FROM notifications WHERE user_id = 10 ORDER BY created_at DESC;

-- 查看评论点赞数
SELECT id, comment_text, likes FROM reviews WHERE id = 5;

-- 查看电影评分
SELECT id, title, average_rating FROM movies WHERE id = 1;
```

---

## ❌ 常见问题排查

### 问题 1: WebSocket 连接失败

**症状:**
```
STOMP Error: ...
WebSocket 错误: ...
```

**排查步骤:**
1. 确认后端已启动（端口 7070）
   ```bash
   curl http://localhost:7070/actuator/health
   ```

2. 检查 CORS 配置
   - 查看 `WebSocketConfig.java`
   - 确认 `setAllowedOrigins` 包含 `http://localhost:8080`

3. 检查用户是否已登录
   ```javascript
   // 控制台执行
   authStore.isAuthenticated  // 应该是 true
   authStore.userId          // 应该有值
   ```

4. 查看浏览器控制台的完整错误信息

### 问题 2: 收不到通知

**症状:**
点赞后，用户A没有收到通知

**排查步骤:**

1. **确认 WebSocket 已连接**
   - 用户A的控制台应该显示 "WebSocket 连接成功"
   - 执行 `websocketService.isConnected()` 应该返回 `true`

2. **确认消息已发送**
   - 后端日志应该显示 "发送点赞通知消息"

3. **确认消息已消费**
   - 后端日志应该显示 "收到点赞通知消息"
   - 后端日志应该显示 "通知已保存到数据库"

4. **检查数据库**
   ```sql
   SELECT * FROM notifications ORDER BY created_at DESC LIMIT 1;
   ```
   应该有新记录

5. **检查 RabbitMQ**
   - 访问 http://localhost:15672
   - 确认 `like.notification` 队列消息数为 0（已被消费）

6. **检查用户ID匹配**
   - 确认推送的 `userId` 和当前登录用户的 `userId` 一致
   - 后端日志：`点赞通知推送成功: authorId=10`
   - 前端控制台：`userId: 10`

### 问题 3: 评分没有更新

**症状:**
发表评论后，电影评分没有变化

**排查步骤:**

1. **检查后端日志**
   应该看到：
   ```
   发送评分更新消息: movieId=1
   收到评分更新消息: movieId=1
   电影评分更新成功: movieId=1, averageRating=X.X
   ```

2. **检查 RabbitMQ**
   - `rating.update` 队列消息应该被消费（消息数为 0）

3. **检查数据库**
   ```sql
   -- 检查评论是否保存
   SELECT * FROM reviews WHERE movie_id = 1;

   -- 检查评分字段
   SELECT id, title, average_rating FROM movies WHERE id = 1;
   ```

4. **检查消费者是否运行**
   - 后端日志应该有 "收到评分更新消息" 日志
   - 如果没有，检查 `RatingUpdateConsumer` 是否正常启动

### 问题 4: 点赞接口调用失败

**症状:**
```
400 Bad Request 或 500 Internal Server Error
```

**原因:**
前端调用点赞接口时没有传递 `likerId` 参数

**解决方法:**
确保前端调用时传递 `likerId`：
```javascript
// 正确的调用方式
await apiService.post(`/api/reviews/${reviewId}/vote?likerId=${currentUserId}`, {
  direction: 'up'
});
```

### 问题 5: 前端编译错误

**症状:**
```
The template requires child element vue/valid-template-root
```

**解决方法:**
已修复！`NotificationHandler.vue` 模板中添加了一个 `<div>` 元素。

---

## ✅ 成功标志

### 评分计算成功
- ✅ 后端日志显示 "电影评分更新成功"
- ✅ 数据库 `movies.average_rating` 字段更新
- ✅ RabbitMQ `rating.update` 队列消息数为 0

### 点赞通知成功
- ✅ 前端弹出通知消息
- ✅ 前端控制台显示 "收到通知"
- ✅ 后端日志显示 "点赞通知推送成功"
- ✅ 数据库 `notifications` 表有新记录
- ✅ 数据库 `reviews.likes` 字段 +1

---

## 📝 测试检查清单

```
功能测试：
□ RabbitMQ 服务启动
□ 后端服务启动（端口 7070）
□ 前端服务启动（端口 8080）
□ 测试端点可访问（/api/test/rating/1）
□ 发表评论触发评分计算
□ 数据库评分字段更新
□ 用户A登录并看到 WebSocket 连接成功
□ 用户B点赞用户A的评论
□ 用户A实时收到通知
□ 通知保存到数据库
□ 评论点赞数 +1
```

---

## 🎉 测试通过标准

所有以下条件都满足，则测试通过：

1. ✅ 评分计算异步执行，不阻塞评论提交
2. ✅ 评分正确计算并保存到数据库
3. ✅ WebSocket 连接成功建立
4. ✅ 点赞后实时推送通知
5. ✅ 通知内容正确（包含点赞者姓名和电影标题）
6. ✅ 通知保存到数据库
7. ✅ 评论点赞数正确更新
8. ✅ 用户登出后 WebSocket 断开
9. ✅ 用户重新登录后 WebSocket 重连

祝测试顺利！🚀
