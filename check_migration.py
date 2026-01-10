#!/usr/bin/env python3
"""
查看需要迁移的图片数量
"""
import mysql.connector

# 数据库配置
MYSQL_CONFIG = {
    'host': 'localhost',
    'user': 'movie_app_user',
    'password': '123456',
    'database': 'movie_db'
}

def check_migration_count():
    """检查需要迁移的数据量"""
    conn = mysql.connector.connect(**MYSQL_CONFIG)
    cursor = conn.cursor(dictionary=True)

    print("=" * 60)
    print("ImgBB 图片迁移数据统计")
    print("=" * 60)

    # 查询各表的图片数量
    tables = [
        ('users', 'profile_image_url', '用户头像'),
        ('movies', 'poster_url', '电影海报'),
        ('actors', 'profile_image_url', '演员照片'),
        ('directors', 'profile_image_url', '导演照片')
    ]

    total_imgbb = 0

    for table_name, column_name, description in tables:
        # 总数
        cursor.execute(f"SELECT COUNT(*) as total FROM {table_name}")
        total = cursor.fetchone()['total']

        # 有图片的数量
        cursor.execute(f"SELECT COUNT(*) as with_image FROM {table_name} WHERE {column_name} IS NOT NULL")
        with_image = cursor.fetchone()['with_image']

        # ImgBB 图片数量
        cursor.execute(f"SELECT COUNT(*) as imgbb_count FROM {table_name} WHERE {column_name} LIKE 'https://i.ibb.co/%'")
        imgbb_count = cursor.fetchone()['imgbb_count']

        # MinIO 图片数量
        cursor.execute(f"SELECT COUNT(*) as minio_count FROM {table_name} WHERE {column_name} LIKE 'http://localhost:9000/%'")
        minio_count = cursor.fetchone()['minio_count']

        print(f"\n[{description}]({table_name}.{column_name})")
        print(f"  总记录数: {total}")
        print(f"  有图片: {with_image}")
        print(f"  - ImgBB: {imgbb_count} 条 [需要迁移]")
        print(f"  - MinIO: {minio_count} 条 [已迁移]")

        total_imgbb += imgbb_count

    print("\n" + "=" * 60)
    print(f"总计需要迁移: {total_imgbb} 张图片")
    print("=" * 60)

    # 显示几个示例 URL
    if total_imgbb > 0:
        print("\n示例 URL（前3条）:")
        cursor.execute("""
            SELECT 'user' AS type, id, profile_image_url AS url
            FROM users
            WHERE profile_image_url LIKE 'https://i.ibb.co/%'
            LIMIT 1

            UNION ALL

            SELECT 'movie' AS type, id, poster_url AS url
            FROM movies
            WHERE poster_url LIKE 'https://i.ibb.co/%'
            LIMIT 1

            UNION ALL

            SELECT 'actor' AS type, id, profile_image_url AS url
            FROM actors
            WHERE profile_image_url LIKE 'https://i.ibb.co/%'
            LIMIT 1
        """)

        for row in cursor.fetchall():
            print(f"  {row['type']:8} ID={row['id']:3} -> {row['url'][:60]}...")

    cursor.close()
    conn.close()

if __name__ == '__main__':
    try:
        check_migration_count()
    except mysql.connector.Error as e:
        print(f"[错误] 数据库连接失败: {e}")
        print("\n请确保:")
        print("1. MySQL 服务正在运行")
        print("2. 数据库配置正确")
        print("3. 已安装 mysql-connector-python: pip install mysql-connector-python")
    except Exception as e:
        print(f"[错误] {e}")
