# ImgBB 到 MinIO 图片迁移指南

## 📋 迁移概述

本文档指导你将现有的 ImgBB 图床图片迁移到自托管的 MinIO 对象存储。

## ✅ 已完成的代码修改

### 1. 后端已就绪
- ✅ MinIO 配置完成 (`MinioConfig.java`)
- ✅ 文件上传服务实现 (`FileUploadService.java`)
- ✅ 上传接口创建 (`FileUploadController.java`)
- ✅ 三个上传端点：
  - `POST /api/upload/avatar` - 用户头像
  - `POST /api/upload/poster` - 电影海报
  - `POST /api/upload/person` - 演员/导演照片

### 2. 前端已更新
- ✅ `AccountSettings.vue` - 用户头像上传改用 MinIO
- ✅ `MovieForm.vue` - 电影海报上传改用 MinIO
- ✅ `PersonForm.vue` - 演员/导演照片上传改用 MinIO
- ✅ `apiService.js` - 添加了上传方法

**重要提示**: 所有 ImgBB 相关代码已注释保留，如需回滚可轻松恢复。

---

## 🗄️ 数据库现状分析

### 需要迁移的表和字段

| 表名 | 字段名 | 描述 | 示例 URL |
|------|--------|------|----------|
| `users` | `profile_image_url` | 用户头像 | `https://i.ibb.co/xxx.jpg` |
| `movies` | `poster_url` | 电影海报 | `https://i.ibb.co/yyy.png` |
| `actors` | `profile_image_url` | 演员照片 | `https://i.ibb.co/zzz.jpg` |
| `directors` | `profile_image_url` | 导演照片 | `https://i.ibb.co/aaa.jpg` |

### 第一步：查看需要迁移的数据量

在 MySQL 中执行以下查询，了解数据量：

```sql
-- 查看各表的图片数量
SELECT
    'users' AS table_name,
    COUNT(*) AS total_count,
    COUNT(profile_image_url) AS with_image_count
FROM users
UNION ALL
SELECT
    'movies',
    COUNT(*),
    COUNT(poster_url)
FROM movies
UNION ALL
SELECT
    'actors',
    COUNT(*),
    COUNT(profile_image_url)
FROM actors
UNION ALL
SELECT
    'directors',
    COUNT(*),
    COUNT(profile_image_url)
FROM directors;
```

### 第二步：导出现有图片 URL

```sql
-- 导出所有需要迁移的图片 URL
SELECT
    'user' AS type,
    id,
    profile_image_url AS url
FROM users
WHERE profile_image_url IS NOT NULL
  AND profile_image_url LIKE 'https://i.ibb.co/%'

UNION ALL

SELECT
    'movie' AS type,
    id,
    poster_url AS url
FROM movies
WHERE poster_url IS NOT NULL
  AND poster_url LIKE 'https://i.ibb.co/%'

UNION ALL

SELECT
    'actor' AS type,
    id,
    profile_image_url AS url
FROM actors
WHERE profile_image_url IS NOT NULL
  AND profile_image_url LIKE 'https://i.ibb.co/%'

UNION ALL

SELECT
    'director' AS type,
    id,
    profile_image_url AS url
FROM directors
WHERE profile_image_url IS NOT NULL
  AND profile_image_url LIKE 'https://i.ibb.co/%'
INTO OUTFILE '/tmp/imgbb_urls.csv'
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n';
```

**注意**: 如果遇到权限问题，可以用 MySQL Workbench 导出结果集为 CSV。

---

## 🔄 迁移策略选择

### 方案 A：渐进式迁移（推荐）⭐

**适用场景**: 生产环境，数据量大，需要平滑过渡

**优点**:
- ✅ 零停机时间
- ✅ 旧数据继续可用
- ✅ 新数据自动使用 MinIO
- ✅ 可随时回滚

**操作步骤**:
1. **现在**: 前端已改用 MinIO，新上传的图片自动存储到 MinIO
2. **之后**: 逐步手动/脚本迁移旧图片
3. **未来**: 当所有图片都迁移后，关闭 ImgBB

**数据库不需要修改** - URL 字段继续存储完整 URL：
- 旧数据: `https://i.ibb.co/xxx.jpg`
- 新数据: `http://localhost:9000/movie-images/avatars/uuid.jpg`

---

### 方案 B：一次性批量迁移

**适用场景**: 测试环境，数据量小（<1000张图片）

**优点**:
- ✅ 彻底迁移
- ✅ 完全自主可控

**缺点**:
- ❌ 需要停机维护
- ❌ 脚本复杂度高
- ❌ 失败风险大

**操作步骤**: 见下方 "批量迁移脚本"

---

## 🛠️ 迁移工具

### 选项 1：手动迁移（适合少量数据）

1. 从 ImgBB 下载图片到本地
2. 通过前端管理界面重新上传
3. 前端会自动上传到 MinIO

**示例**: 迁移某个电影海报
```bash
# 1. 下载图片
wget "https://i.ibb.co/xxx.jpg" -O movie_123.jpg

# 2. 打开管理界面
# 3. 编辑电影，上传新图片
# 4. 保存 - 新 URL 自动更新
```

---

### 选项 2：Python 批量迁移脚本（推荐）

创建文件 `migrate_images.py`:

```python
#!/usr/bin/env python3
"""
ImgBB 图片批量迁移到 MinIO 脚本
"""
import requests
import mysql.connector
import os
from pathlib import Path

# ==================== 配置 ====================
MYSQL_CONFIG = {
    'host': 'localhost',
    'user': 'movie_app_user',
    'password': '123456',
    'database': 'movie_db'
}

MINIO_UPLOAD_ENDPOINTS = {
    'user': 'http://localhost:7070/api/upload/avatar',
    'movie': 'http://localhost:7070/api/upload/poster',
    'actor': 'http://localhost:7070/api/upload/person',
    'director': 'http://localhost:7070/api/upload/person'
}

# 管理员 JWT Token（从浏览器开发者工具获取）
ADMIN_TOKEN = 'YOUR_JWT_TOKEN_HERE'

HEADERS = {
    'Authorization': f'Bearer {ADMIN_TOKEN}'
}

# 临时下载目录
DOWNLOAD_DIR = Path('./temp_images')
DOWNLOAD_DIR.mkdir(exist_ok=True)

# ==================== 函数 ====================

def fetch_records(entity_type):
    """从数据库获取需要迁移的记录"""
    conn = mysql.connector.connect(**MYSQL_CONFIG)
    cursor = conn.cursor(dictionary=True)

    queries = {
        'user': "SELECT id, profile_image_url AS url FROM users WHERE profile_image_url LIKE 'https://i.ibb.co/%'",
        'movie': "SELECT id, poster_url AS url FROM movies WHERE poster_url LIKE 'https://i.ibb.co/%'",
        'actor': "SELECT id, profile_image_url AS url FROM actors WHERE profile_image_url LIKE 'https://i.ibb.co/%'",
        'director': "SELECT id, profile_image_url AS url FROM directors WHERE profile_image_url LIKE 'https://i.ibb.co/%'"
    }

    cursor.execute(queries[entity_type])
    records = cursor.fetchall()
    cursor.close()
    conn.close()
    return records

def download_image(url, save_path):
    """从 ImgBB 下载图片"""
    try:
        response = requests.get(url, timeout=10)
        response.raise_for_status()
        with open(save_path, 'wb') as f:
            f.write(response.content)
        return True
    except Exception as e:
        print(f"  ❌ 下载失败: {e}")
        return False

def upload_to_minio(file_path, entity_type):
    """上传图片到 MinIO"""
    endpoint = MINIO_UPLOAD_ENDPOINTS[entity_type]
    try:
        with open(file_path, 'rb') as f:
            files = {'file': f}
            response = requests.post(endpoint, files=files, headers=HEADERS, timeout=30)
            response.raise_for_status()
            data = response.json()
            return data.get('url')
    except Exception as e:
        print(f"  ❌ 上传失败: {e}")
        return None

def update_database(entity_type, record_id, new_url):
    """更新数据库 URL"""
    conn = mysql.connector.connect(**MYSQL_CONFIG)
    cursor = conn.cursor()

    update_queries = {
        'user': "UPDATE users SET profile_image_url = %s WHERE id = %s",
        'movie': "UPDATE movies SET poster_url = %s WHERE id = %s",
        'actor': "UPDATE actors SET profile_image_url = %s WHERE id = %s",
        'director': "UPDATE directors SET profile_image_url = %s WHERE id = %s"
    }

    cursor.execute(update_queries[entity_type], (new_url, record_id))
    conn.commit()
    cursor.close()
    conn.close()

def migrate_entity(entity_type):
    """迁移某类实体的所有图片"""
    print(f"\n{'='*60}")
    print(f"开始迁移 {entity_type.upper()} 图片...")
    print(f"{'='*60}")

    records = fetch_records(entity_type)
    total = len(records)
    print(f"📊 找到 {total} 条记录需要迁移\n")

    success_count = 0
    fail_count = 0

    for idx, record in enumerate(records, 1):
        record_id = record['id']
        old_url = record['url']

        print(f"[{idx}/{total}] 处理 {entity_type} ID={record_id}")
        print(f"  原 URL: {old_url}")

        # 1. 下载图片
        file_ext = old_url.split('.')[-1].split('?')[0]  # 获取扩展名
        temp_file = DOWNLOAD_DIR / f"{entity_type}_{record_id}.{file_ext}"

        if not download_image(old_url, temp_file):
            fail_count += 1
            continue

        # 2. 上传到 MinIO
        new_url = upload_to_minio(temp_file, entity_type)
        if not new_url:
            fail_count += 1
            os.remove(temp_file)
            continue

        # 3. 更新数据库
        try:
            update_database(entity_type, record_id, new_url)
            print(f"  ✅ 新 URL: {new_url}")
            success_count += 1
        except Exception as e:
            print(f"  ❌ 数据库更新失败: {e}")
            fail_count += 1

        # 4. 删除临时文件
        os.remove(temp_file)

    print(f"\n{'='*60}")
    print(f"迁移完成: 成功 {success_count}/{total}, 失败 {fail_count}")
    print(f"{'='*60}\n")

# ==================== 主程序 ====================
if __name__ == '__main__':
    print("""
╔════════════════════════════════════════════════════════════╗
║         ImgBB → MinIO 图片迁移工具 v1.0                    ║
╚════════════════════════════════════════════════════════════╝
    """)

    # 检查 Token
    if ADMIN_TOKEN == 'YOUR_JWT_TOKEN_HERE':
        print("❌ 请先设置 ADMIN_TOKEN！")
        print("获取方法:")
        print("1. 浏览器登录管理员账号")
        print("2. 打开开发者工具 → Application → Local Storage")
        print("3. 复制 'token' 的值")
        exit(1)

    # 选择迁移类型
    print("请选择要迁移的类型:")
    print("1. 用户头像 (users)")
    print("2. 电影海报 (movies)")
    print("3. 演员照片 (actors)")
    print("4. 导演照片 (directors)")
    print("5. 全部迁移")

    choice = input("\n请输入选项 (1-5): ").strip()

    type_map = {
        '1': ['user'],
        '2': ['movie'],
        '3': ['actor'],
        '4': ['director'],
        '5': ['user', 'movie', 'actor', 'director']
    }

    if choice not in type_map:
        print("❌ 无效选项")
        exit(1)

    # 确认
    confirm = input(f"\n⚠️  即将开始迁移，是否继续？(yes/no): ").strip().lower()
    if confirm != 'yes':
        print("已取消")
        exit(0)

    # 执行迁移
    for entity_type in type_map[choice]:
        migrate_entity(entity_type)

    print("\n✅ 所有迁移任务完成！")
    print(f"临时文件目录: {DOWNLOAD_DIR}")
```

**使用步骤**:

1. **安装依赖**:
```bash
pip install requests mysql-connector-python
```

2. **获取管理员 Token**:
   - 浏览器登录管理员账号
   - F12 打开开发者工具
   - Application → Local Storage
   - 复制 `token` 的值

3. **修改脚本配置**:
   - 填入 `ADMIN_TOKEN`
   - 检查 MySQL 配置

4. **执行迁移**:
```bash
python migrate_images.py
```

5. **验证结果**:
```sql
-- 检查是否还有 ImgBB 的 URL
SELECT COUNT(*) FROM users WHERE profile_image_url LIKE 'https://i.ibb.co/%';
SELECT COUNT(*) FROM movies WHERE poster_url LIKE 'https://i.ibb.co/%';
SELECT COUNT(*) FROM actors WHERE profile_image_url LIKE 'https://i.ibb.co/%';
SELECT COUNT(*) FROM directors WHERE profile_image_url LIKE 'https://i.ibb.co/%';
```

---

## 🔍 验证迁移结果

### 1. MinIO 控制台检查
访问 http://localhost:9001（MinIO Console）
- 用户名: `minioadmin`
- 密码: `minioadmin`

进入 `movie-images` bucket，应该看到：
```
movie-images/
├── avatars/
│   ├── uuid1.jpg
│   ├── uuid2.png
├── posters/
│   ├── uuid3.jpg
├── persons/
│   ├── uuid4.jpg
```

### 2. 前端功能测试
- ✅ 用户设置 - 头像显示正常
- ✅ 电影列表 - 海报显示正常
- ✅ 演员/导演页面 - 照片显示正常
- ✅ 上传新图片 - 能正常上传

### 3. 数据库抽查
```sql
-- 随机抽取几条记录检查 URL 格式
SELECT id, profile_image_url FROM users ORDER BY RAND() LIMIT 5;
SELECT id, poster_url FROM movies ORDER BY RAND() LIMIT 5;
```

---

## ⚠️ 注意事项

### 迁移前
1. **备份数据库**:
```bash
mysqldump -u movie_app_user -p movie_db > backup_$(date +%Y%m%d).sql
```

2. **备份 MinIO 数据** (如果已有数据):
```bash
docker exec minio-dev mc mirror /data/movie-images /backup/movie-images
```

3. **确保 MinIO 容器运行**:
```bash
docker ps | grep minio
```

### 迁移中
1. **分批迁移** - 不要一次迁移所有数据
2. **错误处理** - 脚本应记录失败的 ID
3. **网络超时** - ImgBB 可能限流，设置合理的延迟

### 迁移后
1. **保留 ImgBB 数据** - 至少保留 30 天作为备份
2. **监控 404 错误** - 检查是否有图片加载失败
3. **性能监控** - MinIO 是否正常响应

---

## 🔧 故障排查

### 问题 1: 上传返回 403 Forbidden
**原因**: JWT Token 过期或无权限
**解决**: 重新获取管理员 Token

### 问题 2: MinIO 连接失败
**原因**: MinIO 容器未启动
**解决**:
```bash
docker start minio-dev
```

### 问题 3: 图片显示 404
**原因**: URL 格式错误或 bucket 权限问题
**解决**:
```bash
# 进入 MinIO 容器设置公开访问
docker exec -it minio-dev mc anonymous set public movie-images
```

### 问题 4: 数据库连接失败
**原因**: MySQL 配置错误
**解决**: 检查 `migrate_images.py` 中的 `MYSQL_CONFIG`

---

## 📊 迁移进度追踪

建议创建一个迁移日志表：

```sql
CREATE TABLE migration_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(20),
    entity_id BIGINT,
    old_url VARCHAR(2048),
    new_url VARCHAR(2048),
    status VARCHAR(20),  -- 'success', 'failed', 'pending'
    error_message TEXT,
    migrated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

在脚本中记录每次迁移操作，方便追踪和重试。

---

## 🎯 推荐迁移流程

### 阶段 1: 准备（今天）
- ✅ 后端代码已部署
- ✅ 前端代码已更新
- ✅ MinIO 已配置
- ✅ 备份数据库

### 阶段 2: 测试（明天）
1. 创建测试数据
2. 手动迁移 2-3 条记录
3. 验证前端显示
4. 验证新上传功能

### 阶段 3: 小规模试运行（3 天后）
1. 运行脚本迁移 10% 的数据
2. 监控 1-2 天
3. 收集反馈

### 阶段 4: 全量迁移（1 周后）
1. 选择低峰期（凌晨）
2. 运行完整迁移脚本
3. 实时监控

### 阶段 5: 清理（2 周后）
1. 确认所有功能正常
2. 停用 ImgBB（但保留数据备份）
3. 移除前端注释的旧代码（可选）

---

## 📝 总结

### 当前状态
- ✅ **新图片**: 已自动使用 MinIO
- ⏳ **旧图片**: 仍存储在 ImgBB（需要迁移）

### 推荐方案
**渐进式迁移**（方案 A）:
- 旧数据继续用 ImgBB（不影响访问）
- 新数据自动用 MinIO
- 有时间慢慢迁移旧数据

### 需要你决定的
1. 是否现在就迁移旧数据？
2. 选择手动迁移还是脚本迁移？
3. 迁移后是否立即删除 ImgBB 数据？

---

## 📞 需要帮助？

如果遇到问题，请提供以下信息：
1. 数据库中图片总数（各表）
2. 错误日志截图
3. MinIO 容器状态 (`docker ps`)
4. 后端日志（`backend/logs/spring.log`）

祝迁移顺利！🚀
