# Nacos 服务注册配置指南

## 📋 已完成的配置

### 1. **依赖添加** (`pom.xml`)
- 添加了 `spring-cloud-starter-alibaba-nacos-discovery` 依赖
- 版本：2023.0.1.2（兼容 Spring Boot 3.5.0）

### 2. **配置文件** (`application.properties`)
```properties
spring.application.name=movie-backend
spring.cloud.nacos.discovery.server-addr=localhost:8848
spring.cloud.nacos.discovery.namespace=public
spring.cloud.nacos.discovery.enabled=true
spring.cloud.nacos.discovery.ip=127.0.0.1
spring.cloud.nacos.discovery.port=${server.port}
```

### 3. **主程序注解** (`BackendApplication.java`)
- 添加了 `@EnableDiscoveryClient` 注解
- 添加了详细的功能集成说明注释

---

## 🚀 启动步骤

### 1. 确保 Nacos 服务运行

在 **WSL Debian** 中检查 Nacos 容器状态：

```bash
docker ps | grep nacos
```

如果没有运行，启动你的 `dockercompose.yml` 中的 Nacos 服务。

### 2. 重新加载 Maven 依赖

在 IDE 中：
- 右键点击 `pom.xml`
- 选择 **Maven -> Reload Project**
- 等待依赖下载完成

或者在终端中：
```bash
cd backend
mvn clean install
```

### 3. 启动后端服务

```bash
cd backend
mvn spring-boot:run
```

或在 IDE 中运行 `BackendApplication.java`

### 4. 查看启动日志

启动成功后，你应该能看到类似的日志：

```
INFO c.a.n.c.r.client - [REGISTER-SERVICE] public registering service movie-backend with instance ...
INFO c.a.n.c.r.client - [REGISTER-SERVICE] public register service movie-backend success
```

---

## 🌐 访问 Nacos 控制台

### 1. 打开浏览器

访问：**http://localhost:8848/nacos**

### 2. 登录

- **用户名**：`nacos`
- **密码**：`nacos`

### 3. 查看服务列表

登录后，按照以下步骤：

1. 点击左侧菜单：**服务管理** → **服务列表**
2. 在服务列表中找到 **`movie-backend`**
3. 查看服务详情：
   - **服务名**：movie-backend
   - **分组**：DEFAULT_GROUP
   - **集群数**：1
   - **实例数**：1
   - **健康实例数**：1

### 4. 查看实例详情

点击 **详情** 按钮，可以看到：

| 字段 | 值 |
|------|-----|
| IP | 127.0.0.1 |
| 端口 | 7070 |
| 权重 | 1.0 |
| 健康状态 | ✅ 健康 |
| 元数据 | preserved.register.source=SPRING_CLOUD |

---

## ✅ 验证服务注册成功

### 方法1：Nacos 控制台
- 服务列表中显示 `movie-backend`
- 实例数 = 1
- 健康实例数 = 1（绿色）

### 方法2：后端日志
查看后端启动日志，应该包含：
```
[REGISTER-SERVICE] public register service movie-backend success
```

### 方法3：测试接口
访问：http://localhost:7070/api/movies/hot?limit=5

如果能正常返回数据，说明服务运行正常。

---

## 🎯 Nacos 的作用

在你的单体项目中，Nacos 主要用于：

1. **服务监控**：实时查看后端服务是否在线
2. **健康检查**：Nacos 会定期检测服务健康状态
3. **快速定位问题**：如果服务下线，Nacos 控制台会立即显示

### 未来可扩展的功能：

如果项目升级为微服务架构，Nacos 还能提供：
- **服务发现**：多个服务之间互相调用
- **配置管理**：集中管理所有服务的配置文件
- **负载均衡**：多个实例之间的流量分配

---

## 🛠️ 常见问题

### 1. 服务注册失败

**错误日志**：
```
Connection refused: localhost:8848
```

**解决方案**：
- 检查 Nacos 容器是否启动：`docker ps | grep nacos`
- 确认端口映射正确：`8848:8848`

### 2. 服务列表中找不到 movie-backend

**可能原因**：
- 后端启动失败（检查启动日志）
- Nacos 配置错误（检查 `application.properties`）
- 依赖未正确加载（重新 `mvn clean install`）

**解决方案**：
1. 重启后端服务
2. 查看后端日志是否有 Nacos 相关错误
3. 确认 `@EnableDiscoveryClient` 注解存在

### 3. 实例显示不健康（红色）

**可能原因**：
- 后端服务崩溃
- 网络问题
- 健康检查接口失败

**解决方案**：
- 检查后端日志
- 访问 http://localhost:7070 确认服务可用
- 重启后端服务

---

## 📊 Nacos 控制台截图说明

### 服务列表页面

你应该看到类似这样的界面：

| 服务名 | 分组 | 集群数 | 实例数 | 健康实例数 | 操作 |
|--------|------|--------|--------|------------|------|
| movie-backend | DEFAULT_GROUP | 1 | 1 | 1 ✅ | 详情/编辑/删除 |

### 实例详情页面

点击"详情"后，可以看到：

- **基本信息**：IP、端口、权重
- **健康状态**：健康/不健康
- **元数据**：注册来源、版本等
- **操作**：下线/权重调整

---

## 🎉 完成！

现在你的后端服务已经成功注册到 Nacos，可以通过 Nacos 控制台实时监控服务运行状态！

如需帮助，请查看：
- Nacos 官方文档：https://nacos.io/zh-cn/docs/quick-start.html
- Spring Cloud Alibaba 文档：https://spring-cloud-alibaba-group.github.io/
