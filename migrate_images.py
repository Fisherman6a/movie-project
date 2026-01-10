#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
ImgBB 图片批量迁移到 MinIO 脚本
"""
import requests
import mysql.connector
import os
from pathlib import Path
import time

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

# 管理员 JWT Token（需要手动填入）
ADMIN_TOKEN = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJST0xFX0FETUlOIiwidXNlcklkIjo1ODAsImlhdCI6MTc2ODAzNjQ3MSwiZXhwIjoxNzY4MTIyODcxfQ.OEc-WBFulseMJVIiC4GP3MuVheDsopHT6WyGXoA6_ug'

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
        'user': "SELECT id, profile_image_url AS url FROM users WHERE profile_image_url LIKE 'https://i.ibb.co/%' OR profile_image_url LIKE 'https://ibb.co/%'",
        'movie': "SELECT id, poster_url AS url FROM movies WHERE poster_url LIKE 'https://i.ibb.co/%' OR poster_url LIKE 'https://ibb.co/%'",
        'actor': "SELECT id, profile_image_url AS url FROM actors WHERE profile_image_url LIKE 'https://i.ibb.co/%' OR profile_image_url LIKE 'https://ibb.co/%'",
        'director': "SELECT id, profile_image_url AS url FROM directors WHERE profile_image_url LIKE 'https://i.ibb.co/%' OR profile_image_url LIKE 'https://ibb.co/%'"
    }

    cursor.execute(queries[entity_type])
    records = cursor.fetchall()
    cursor.close()
    conn.close()
    return records

def download_image(url, save_path):
    """从 ImgBB 下载图片"""
    try:
        # 添加重试机制
        for attempt in range(3):
            try:
                response = requests.get(url, timeout=30)
                response.raise_for_status()
                with open(save_path, 'wb') as f:
                    f.write(response.content)
                return True
            except requests.RequestException as e:
                if attempt == 2:  # 最后一次尝试
                    print(f"  [错误] 下载失败 (尝试 {attempt + 1}/3): {e}")
                    return False
                time.sleep(2)  # 重试前等待
        return False
    except Exception as e:
        print(f"  [错误] 下载失败: {e}")
        return False

def upload_to_minio(file_path, entity_type):
    """上传图片到 MinIO"""
    endpoint = MINIO_UPLOAD_ENDPOINTS[entity_type]
    try:
        # 根据文件扩展名确定Content-Type
        ext = file_path.suffix.lower()
        content_type_map = {
            '.jpg': 'image/jpeg',
            '.jpeg': 'image/jpeg',
            '.png': 'image/png',
            '.gif': 'image/gif',
            '.webp': 'image/webp'
        }
        content_type = content_type_map.get(ext, 'image/jpeg')

        with open(file_path, 'rb') as f:
            # 明确指定文件名和Content-Type
            files = {
                'file': (file_path.name, f, content_type)
            }
            response = requests.post(endpoint, files=files, headers=HEADERS, timeout=30)
            response.raise_for_status()
            data = response.json()
            return data.get('url')
    except requests.HTTPError as e:
        if e.response.status_code == 403:
            print(f"  [错误] 上传失败: 403 Forbidden - JWT Token可能已过期")
        elif e.response.status_code == 400:
            try:
                error_msg = e.response.json()
                print(f"  [错误] 上传失败: HTTP 400 - {error_msg}")
            except:
                print(f"  [错误] 上传失败: HTTP 400 - {e.response.text[:200]}")
        else:
            print(f"  [错误] 上传失败: HTTP {e.response.status_code} - {e.response.text[:200]}")
        return None
    except Exception as e:
        print(f"  [错误] 上传失败: {e}")
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
    print(f"\n{'=' * 60}")
    print(f"开始迁移 {entity_type.upper()} 图片...")
    print(f"{'=' * 60}")

    records = fetch_records(entity_type)
    total = len(records)

    if total == 0:
        print(f"没有需要迁移的 {entity_type} 图片")
        return 0, 0

    print(f"找到 {total} 条记录需要迁移\n")

    success_count = 0
    fail_count = 0
    failed_ids = []

    for idx, record in enumerate(records, 1):
        record_id = record['id']
        old_url = record['url']

        print(f"[{idx}/{total}] 处理 {entity_type} ID={record_id}")
        print(f"  原 URL: {old_url[:80]}...")

        # 1. 下载图片
        # 获取文件扩展名，处理可能的URL参数
        url_path = old_url.split('?')[0]
        file_ext = url_path.split('.')[-1] if '.' in url_path else 'jpg'
        # 确保扩展名合法
        if file_ext not in ['jpg', 'jpeg', 'png', 'gif', 'webp']:
            file_ext = 'jpg'

        temp_file = DOWNLOAD_DIR / f"{entity_type}_{record_id}.{file_ext}"

        if not download_image(old_url, temp_file):
            fail_count += 1
            failed_ids.append(record_id)
            continue

        # 2. 上传到 MinIO
        new_url = upload_to_minio(temp_file, entity_type)
        if not new_url:
            fail_count += 1
            failed_ids.append(record_id)
            # 删除临时文件
            if temp_file.exists():
                os.remove(temp_file)
            continue

        # 3. 更新数据库
        try:
            update_database(entity_type, record_id, new_url)
            print(f"  [成功] 新 URL: {new_url}")
            success_count += 1
        except Exception as e:
            print(f"  [错误] 数据库更新失败: {e}")
            fail_count += 1
            failed_ids.append(record_id)

        # 4. 删除临时文件
        if temp_file.exists():
            os.remove(temp_file)

        # 添加小延迟，避免请求过快
        time.sleep(0.5)

    print(f"\n{'=' * 60}")
    print(f"迁移完成: 成功 {success_count}/{total}, 失败 {fail_count}")
    if failed_ids:
        print(f"失败的ID: {failed_ids}")
    print(f"{'=' * 60}\n")

    return success_count, fail_count

# ==================== 主程序 ====================
if __name__ == '__main__':
    print("""
============================================================
         ImgBB -> MinIO 图片迁移工具 v1.0
============================================================
    """)

    # 检查 Token
    if ADMIN_TOKEN == 'YOUR_JWT_TOKEN_HERE':
        print("[错误] 请先设置 ADMIN_TOKEN!")
        print("\n获取方法:")
        print("1. 浏览器登录管理员账号")
        print("2. F12 打开开发者工具")
        print("3. Application -> Local Storage -> http://localhost:7070")
        print("4. 找到 'token' 字段，复制其值")
        print("5. 粘贴到脚本的 ADMIN_TOKEN 变量中")
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
        print("[错误] 无效选项")
        exit(1)

    # 确认
    confirm = input(f"\n[警告] 即将开始迁移，是否继续？(yes/no): ").strip().lower()
    if confirm != 'yes':
        print("已取消")
        exit(0)

    # 执行迁移
    total_success = 0
    total_fail = 0

    for entity_type in type_map[choice]:
        success, fail = migrate_entity(entity_type)
        total_success += success
        total_fail += fail

    print("\n" + "=" * 60)
    print(f"所有迁移任务完成!")
    print(f"总计: 成功 {total_success} 张, 失败 {total_fail} 张")
    print(f"临时文件目录: {DOWNLOAD_DIR}")
    print("=" * 60)

    if total_fail > 0:
        print("\n[提示] 有失败的迁移，请检查:")
        print("1. JWT Token 是否有效")
        print("2. 后端服务是否正常运行")
        print("3. MinIO 容器是否正常运行")
        print("4. 网络连接是否正常")
