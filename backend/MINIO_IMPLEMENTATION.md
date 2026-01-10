# MinIO 对象存储集成文档

## 📋 概述

项目已成功集成 MinIO 对象存储服务，替代了原有的 ImgBB 图床。MinIO 是高性能的自建对象存储服务，提供更好的控制性和安全性。

## 🏗️ 架构设计

```
用户上传文件 (前端)
    ↓
POST /api/upload/avatar (后端)
    ↓
FileUploadService
    ↓
MinIO 对象存储
    ↓
返回文件 URL
```

## 🚀 MinIO 服务信息

根据你的 Docker 容器配置：

- **容器名称**: `minio-dev`
- **镜像版本**: `minio/minio:RELEASE.2025-09-07T16-13-09Z`
- **API 端口**: `9000` (程序访问)
- **Console 端口**: `9001` (Web 管理界面)
- **默认账号**: `minioadmin`
- **默认密码**: `minioadmin`

### 访问地址

- **API**: http://localhost:9000
- **Web Console**: http://localhost:9001
- **Bucket**: `movie-images`

## 📁 文件组织结构

MinIO 中的文件按照以下结构存储：

```
movie-images/           # Bucket 名称
├── avatars/           # 用户头像
│   ├── uuid1.jpg
│   └── uuid2.png
├── posters/           # 电影海报
│   ├── uuid3.jpg
│   └── uuid4.png
└── persons/           # 演员/导演照片
    ├── uuid5.jpg
    └── uuid6.png
```

## 🔧 后端实现

### 1. 配置类 ([MinioConfig.java](d:\Code\Development\movie-project\backend\src\main\java\com\movie_back\backend\config\MinioConfig.java))

```java
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {
    private String endpoint;        // http://localhost:9000
    private String accessKey;       // minioadmin
    private String secretKey;       // minioadmin
    private String bucketName;      // movie-images
    private String urlPrefix;       // http://localhost:9000/movie-images

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
```

### 2. 文件上传服务 ([FileUploadService.java](d:\Code\Development\movie-project\backend\src\main\java\com\movie_back\backend\service\FileUploadService.java))

**核心功能:**
- ✅ 自动创建和配置 Bucket
- ✅ 文件类型验证（只允许图片）
- ✅ 文件大小限制（5MB）
- ✅ UUID 生成唯一文件名
- ✅ 设置 Bucket 为公开访问
- ✅ 文件删除功能

**主要方法:**

```java
// 上传用户头像
public String uploadAvatar(MultipartFile file) throws Exception

// 上传电影海报
public String uploadPoster(MultipartFile file) throws Exception

// 上传演员/导演照片
public String uploadPersonPhoto(MultipartFile file) throws Exception

// 通用上传
public String uploadFile(MultipartFile file, String folder) throws Exception

// 删除文件
public void deleteFile(String fileUrl)
```

### 3. 上传接口 ([FileUploadController.java](d:\Code\Development\movie-project\backend\src\main\java\com\movie_back\backend\controller\FileUploadController.java))

| 接口 | 路径 | 权限 | 用途 |
|------|------|------|------|
| 上传头像 | `POST /api/upload/avatar` | 需要登录 | 用户更换头像 |
| 上传海报 | `POST /api/upload/poster` | 需要管理员 | 添加/编辑电影海报 |
| 上传人物照片 | `POST /api/upload/person` | 需要管理员 | 添加/编辑演员/导演照片 |
| 通用上传 | `POST /api/upload/file` | 需要登录 | 其他文件上传 |

## 🎨 前端集成

### 1. API 服务 ([apiService.js](d:\Code\Development\movie-project\frontend\src\services\apiService.js))

```javascript
// 上传用户头像
uploadAvatar(formData) {
    return apiClient.post('/upload/avatar', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    });
}

// 上传电影海报
uploadPoster(formData) {
    return apiClient.post('/upload/poster', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    });
}
```

### 2. 使用示例 ([AccountSettings.vue](d:\Code\Development\movie-project\frontend\src\views\AccountSettings.vue))

```javascript
const customAvatarUploadRequest = async ({ file, onFinish, onError }) => {
  uploadingToHost.value = true;
  const formData = new FormData();
  formData.append("file", file.file);

  try {
    const response = await apiService.uploadAvatar(formData);
    profileForm.value.profileImageUrl = response.data.url;
    message.success("头像预览更新成功");
    onFinish();
  } catch (error) {
    message.error("头像上传失败: " + error.message);
    onError();
  } finally {
    uploadingToHost.value = false;
  }
};
```

## 📝 使用指南

### 启动 MinIO（已运行）

你的 MinIO 已经在 Docker 中运行，无需额外操作：

```bash
# 检查容器状态
docker ps | grep minio

# 如需重启
docker restart minio-dev

# 查看日志
docker logs minio-dev
```

### 访问 Web 管理界面

1. 浏览器访问: http://localhost:9001
2. 登录账号: `minioadmin`
3. 登录密码: `minioadmin`
4. 可以在这里管理文件、查看统计、配置权限等

### 测试上传功能

1. 启动后端服务
2. 启动前端服务
3. 登录账号
4. 进入"账号设置"页面
5. 点击"更换头像"按钮
6. 选择图片上传
7. 查看 MinIO Console 中是否出现新文件

### 响应格式

**成功响应:**
```json
{
  "status": "success",
  "url": "http://localhost:9000/movie-images/avatars/uuid-xxx.jpg",
  "message": "头像上传成功"
}
```

**失败响应:**
```json
{
  "status": "error",
  "message": "文件大小不能超过 5MB"
}
```

## ⚙️ 配置说明

### application.properties

```properties
# MinIO 对象存储配置
minio.endpoint=http://localhost:9000
minio.access-key=minioadmin
minio.secret-key=minioadmin
minio.bucket-name=movie-images
minio.url-prefix=http://localhost:9000/movie-images
```

### 配置项说明

| 配置项 | 说明 | 默认值 |
|-------|------|--------|
| `minio.endpoint` | MinIO API 地址 | http://localhost:9000 |
| `minio.access-key` | 访问密钥 | minioadmin |
| `minio.secret-key` | 密钥 | minioadmin |
| `minio.bucket-name` | 存储桶名称 | movie-images |
| `minio.url-prefix` | 文件访问URL前缀 | http://localhost:9000/movie-images |

##⚠️ 注意事项

### 1. 文件限制

- **支持格式**: 仅图片文件 (image/*)
- **文件大小**: 最大 5MB
- **文件命名**: 自动使用 UUID，防止冲突

### 2. Bucket 权限

- Bucket `movie-images` 被设置为**公开读取**
- 任何人都可以通过 URL 直接访问图片
- 只有认证用户才能上传文件

### 3. 生产环境建议

```properties
# 生产环境配置示例
minio.endpoint=https://minio.yourdomain.com
minio.access-key=your-production-key
minio.secret-key=your-production-secret
minio.bucket-name=movie-images-prod
minio.url-prefix=https://cdn.yourdomain.com/movie-images
```

建议使用：
- ✅ HTTPS 加密传输
- ✅ 强密码
- ✅ CDN 加速
- ✅ 定期备份
- ✅ 访问日志监控

### 4. 迁移注意

如果你需要从 ImgBB 迁移已有图片到 MinIO：

1. 下载 ImgBB 上的图片
2. 使用 MinIO Console 批量上传
3. 更新数据库中的图片 URL

## 🔍 故障排查

### 问题 1: 上传失败 "Connection refused"

**原因**: MinIO 服务未启动

**解决**:
```bash
docker start minio-dev
```

### 问题 2: 上传成功但无法访问图片

**原因**: Bucket 权限未设置为公开

**解决**: 后端服务启动时会自动设置，或手动在 MinIO Console 中设置 Bucket Policy

### 问题 3: 文件上传后找不到

**原因**: Bucket 名称配置不匹配

**解决**: 检查 `application.properties` 中的 `minio.bucket-name` 是否正确

## 📊 对比 ImgBB vs MinIO

| 特性 | ImgBB | MinIO |
|------|-------|-------|
| 部署方式 | 第三方服务 | 自建服务 |
| 成本 | 免费/付费 | 免费（自己服务器） |
| 安全性 | API Key 暴露风险 | 完全可控 |
| 速度 | 依赖外网 | 本地网络速度快 |
| 控制权 | ❌ 无 | ✅ 完全控制 |
| 隐私性 | ❌ 数据在第三方 | ✅ 数据在自己服务器 |
| 可靠性 | 依赖第三方 | 依赖自己维护 |

## 🎯 总结

✅ **已完成:**
1. MinIO Maven 依赖集成
2. MinIO 配置类
3. 文件上传服务（验证、限制、UUID命名）
4. 上传 API 接口（头像、海报、人物照片）
5. 前端上传功能改造
6. 自动 Bucket 创建和权限设置

✅ **特性:**
- 自建对象存储，完全可控
- 文件类型和大小验证
- UUID 防止文件名冲突
- 公开访问URL
- 简单易用的 API

🎉 **现在你的项目已经使用 MinIO 作为图床，安全性和可控性大大提升！**
