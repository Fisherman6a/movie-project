# Elasticsearch 集成实现文档

## 📋 概述

本项目已完整集成 Elasticsearch,用于提供高性能的全文搜索和综合查询功能。

## 🏗️ 架构设计

### 1. 数据流向

```
MySQL (主数据库)
    ↓
电影 CRUD 操作 (MovieService)
    ↓
发送 RabbitMQ 消息 (MessageProducerService)
    ↓
IndexUpdateConsumer 消费消息
    ↓
更新 Elasticsearch 索引 (ElasticsearchService)
```

### 2. 核心组件

#### 后端文件结构
```
backend/
├── document/
│   └── MovieDocument.java          # ES 文档实体
├── repository/
│   └── MovieSearchRepository.java  # ES 仓库接口
├── service/
│   ├── ElasticsearchService.java   # ES 搜索服务
│   └── ElasticsearchIndexInitializer.java  # 索引初始化器
├── consumer/
│   └── IndexUpdateConsumer.java    # RabbitMQ 消费者
├── controller/
│   ├── SearchController.java       # 搜索接口
│   └── AdminController.java        # 管理接口
└── config/
    └── RabbitMQConfig.java         # 已配置 index.update 队列
```

## 🚀 使用指南

### 前置条件

1. **安装 Elasticsearch**
```bash
# Docker 方式（推荐）
docker run -d \
  --name elasticsearch \
  -p 9200:9200 \
  -p 9300:9300 \
  -e "discovery.type=single-node" \
  -e "xpack.security.enabled=false" \
  docker.elastic.co/elasticsearch/elasticsearch:8.11.0
```

2. **验证 ES 运行**
```bash
curl http://localhost:9200
# 应该返回 ES 版本信息
```

### 初始化索引

#### 方法一：手动触发（推荐）
使用管理员账号登录后,调用重建索引接口:
```bash
POST http://localhost:7070/api/admin/es/rebuild-index
Authorization: Bearer <admin-token>
```

#### 方法二：环境变量自动初始化
```bash
# 启动应用时设置环境变量
export ES_INIT_INDEX=true
mvn spring-boot:run
```

## 🔍 API 接口

### 1. 综合搜索（推荐使用）
搜索电影标题、演员、导演、简介、类型等所有字段

```http
GET /api/search/comprehensive?keyword=肖申克&page=0&size=20
```

**响应示例:**
```json
[
  {
    "id": 1,
    "title": "肖申克的救赎",
    "releaseYear": 1994,
    "genre": "剧情",
    "country": "美国",
    "averageRating": 9.7,
    "actorNames": ["蒂姆·罗宾斯", "摩根·弗里曼"],
    "directorNames": ["弗兰克·德拉邦特"],
    "posterUrl": "https://...",
    "synopsis": "..."
  }
]
```

### 2. 高级搜索（支持多条件过滤）
```http
GET /api/search/advanced?keyword=肖申克&genre=剧情&minRating=8.0&yearStart=1990&yearEnd=2000
```

**参数说明:**
- `keyword`: 关键词（搜索标题、演员、导演、简介）
- `genre`: 类型过滤
- `country`: 国家过滤
- `minRating`: 最低评分
- `yearStart`: 起始年份
- `yearEnd`: 结束年份
- `page`: 页码（默认 0）
- `size`: 每页数量（默认 20）

### 3. 按标题搜索
```http
GET /api/search/by-title?title=肖申克
```

### 4. 按演员搜索
```http
GET /api/search/by-actor?name=蒂姆·罗宾斯
```

### 5. 按导演搜索
```http
GET /api/search/by-director?name=弗兰克·德拉邦特
```

### 6. 管理接口（需要 ADMIN 权限）

#### 重建全部索引
```http
POST /api/admin/es/rebuild-index
Authorization: Bearer <admin-token>
```

#### 索引单个电影
```http
POST /api/admin/es/index/{movieId}
Authorization: Bearer <admin-token>
```

#### 删除单个电影索引
```http
DELETE /api/admin/es/index/{movieId}
Authorization: Bearer <admin-token>
```

## ⚙️ 自动索引更新

### 触发时机
当执行以下操作时,系统会自动通过 RabbitMQ 更新 ES 索引:

1. **创建电影** - 发送 `CREATE` 消息
2. **更新电影** - 发送 `UPDATE` 消息
3. **删除电影** - 发送 `DELETE` 消息
4. **评分更新** - 发送 `UPDATE` 消息（同步最新评分到 ES）

### RabbitMQ 消息队列
- **队列名**: `index.update`
- **交换机**: `movie.exchange`
- **路由键**: `index.update`
- **消息格式**:
```json
{
  "movieId": 1,
  "operation": "CREATE|UPDATE|DELETE",
  "timestamp": 1704067200000
}
```

## 🔄 MySQL vs Elasticsearch

### 原有 MySQL 搜索（仍然可用）
```http
GET /api/movies/search?title=肖申克
GET /api/movies/by-actor?name=蒂姆·罗宾斯
GET /api/movies/by-director?name=弗兰克
```

### 新增 Elasticsearch 搜索（推荐）
```http
GET /api/search/comprehensive?keyword=肖申克
GET /api/search/by-actor?name=蒂姆·罗宾斯
GET /api/search/by-director?name=弗兰克
```

### 两者区别

| 特性 | MySQL | Elasticsearch |
|------|-------|---------------|
| 搜索方式 | LIKE 查询 | 全文搜索 |
| 中文分词 | ❌ 不支持 | ✅ 支持 (ik_max_word) |
| 模糊匹配 | ⚠️ 性能差 | ✅ 高性能 |
| 综合搜索 | ❌ 需要多个查询 | ✅ 一次查询多字段 |
| 性能 | 数据量大时慢 | 毫秒级响应 |
| 排序 | ✅ 支持 | ✅ 支持 + 相关性评分 |

## 📝 注意事项

### 1. 索引一致性
- ES 通过 RabbitMQ 异步更新,可能有短暂延迟（通常 <100ms）
- 如需强一致性,可以在更新后调用管理接口手动刷新

### 2. 中文分词
目前使用的是 `ik_max_word` 分词器（细粒度分词）
- 如果 ES 没有安装 IK 插件,需要先安装:
```bash
# 进入 ES 容器
docker exec -it elasticsearch bash

# 安装 IK 分词器
./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v8.11.0/elasticsearch-analysis-ik-8.11.0.zip

# 重启容器
docker restart elasticsearch
```

如果不使用 IK 分词器,需要修改 `MovieDocument.java` 中的 analyzer:
```java
@Field(type = FieldType.Text, analyzer = "standard")  // 改为标准分词器
```

### 3. 性能优化建议
- **生产环境**: 建议使用 ES 集群
- **索引策略**: 可以定期（如每天凌晨）重建索引
- **缓存**: 热门搜索词可以加 Redis 缓存

### 4. 故障处理
如果 ES 不可用,系统不会影响正常功能:
- 搜索会失败但不会崩溃
- 索引更新消息会在 RabbitMQ 中重试
- 可以继续使用原有的 MySQL 搜索接口

## 🧪 测试流程

1. **启动 Elasticsearch**
```bash
docker start elasticsearch
```

2. **启动后端应用**
```bash
cd backend
mvn spring-boot:run
```

3. **重建索引（首次使用）**
```bash
# 使用 admin 账号登录获取 token
POST http://localhost:7070/api/auth/login
{
  "username": "admin",
  "password": "admin123"
}

# 使用返回的 token 重建索引
POST http://localhost:7070/api/admin/es/rebuild-index
Authorization: Bearer <token>
```

4. **测试搜索**
```bash
# 综合搜索
GET http://localhost:7070/api/search/comprehensive?keyword=肖申克

# 按演员搜索
GET http://localhost:7070/api/search/by-actor?name=蒂姆

# 高级搜索
GET http://localhost:7070/api/search/advanced?keyword=剧情&minRating=8.0&yearStart=1990
```

## 🎯 总结

✅ **已完成:**
1. Elasticsearch 文档实体 (MovieDocument)
2. ES 仓库接口 (MovieSearchRepository)
3. ES 搜索服务 (ElasticsearchService)
4. RabbitMQ 消费者 (IndexUpdateConsumer)
5. 搜索 API 接口 (SearchController)
6. 管理 API 接口 (AdminController)
7. 自动索引更新（CREATE/UPDATE/DELETE）
8. 索引初始化工具

✅ **特性:**
- 全文搜索（标题、演员、导演、简介、类型）
- 高级多条件过滤搜索
- 异步索引更新（通过 RabbitMQ）
- 手动索引管理接口
- 兼容原有 MySQL 搜索

🎉 **现在你的项目已经真正使用了 Elasticsearch 进行综合搜索!**
