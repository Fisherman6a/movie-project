#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试上传单个图片到 MinIO
"""
import requests

# JWT Token
ADMIN_TOKEN = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJST0xFX0FETUlOIiwidXNlcklkIjo1ODAsImlhdCI6MTc2ODAzNjQ3MSwiZXhwIjoxNzY4MTIyODcxfQ.OEc-WBFulseMJVIiC4GP3MuVheDsopHT6WyGXoA6_ug'

# 测试URL
test_image_url = 'https://i.ibb.co/xtMwLQdv/userdefault-1.jpg'

print("1. 下载测试图片...")
response = requests.get(test_image_url, timeout=10)
print(f"   状态: {response.status_code}")
print(f"   类型: {response.headers.get('Content-Type')}")
print(f"   大小: {len(response.content)} bytes")

# 保存到临时文件
with open('test_image.jpg', 'wb') as f:
    f.write(response.content)
print("   已保存到 test_image.jpg")

print("\n2. 上传到 MinIO...")
upload_url = 'http://localhost:7070/api/upload/avatar'
headers = {'Authorization': f'Bearer {ADMIN_TOKEN}'}

with open('test_image.jpg', 'rb') as f:
    # 明确指定文件名和Content-Type
    files = {'file': ('test_image.jpg', f, 'image/jpeg')}
    response = requests.post(upload_url, files=files, headers=headers, timeout=30)

print(f"   状态码: {response.status_code}")
print(f"   响应头: {dict(response.headers)}")
print(f"   响应内容: {response.text}")

if response.status_code == 200:
    data = response.json()
    print(f"\n[成功] 上传成功!")
    print(f"   新URL: {data.get('url')}")
else:
    print(f"\n[失败] 上传失败")
    if response.status_code == 400:
        print("   可能的原因:")
        print("   1. 文件格式不支持")
        print("   2. 文件大小超限")
        print("   3. 文件内容有问题")
        print("   4. MultipartFile 参数名不对")
