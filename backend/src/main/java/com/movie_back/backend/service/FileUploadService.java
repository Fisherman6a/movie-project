package com.movie_back.backend.service;

import com.movie_back.backend.config.MinioConfig;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.UUID;

/**
 * 文件上传服务
 * 使用 MinIO 对象存储
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    /**
     * 应用启动时检查并创建 bucket
     */
    @PostConstruct
    public void init() {
        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .build()
            );

            if (!bucketExists) {
                // 创建 bucket
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(minioConfig.getBucketName())
                                .build()
                );
                log.info("MinIO bucket 创建成功: {}", minioConfig.getBucketName());

                // 设置 bucket 为公开访问（用于图片直接访问）
                String policy = """
                        {
                            "Version": "2012-10-17",
                            "Statement": [
                                {
                                    "Effect": "Allow",
                                    "Principal": {"AWS": ["*"]},
                                    "Action": ["s3:GetObject"],
                                    "Resource": ["arn:aws:s3:::%s/*"]
                                }
                            ]
                        }
                        """.formatted(minioConfig.getBucketName());

                minioClient.setBucketPolicy(
                        SetBucketPolicyArgs.builder()
                                .bucket(minioConfig.getBucketName())
                                .config(policy)
                                .build()
                );
                log.info("MinIO bucket 已设置为公开访问");
            } else {
                log.info("MinIO bucket 已存在: {}", minioConfig.getBucketName());
            }
        } catch (Exception e) {
            log.error("初始化 MinIO bucket 失败", e);
        }
    }

    /**
     * 上传文件
     *
     * @param file     文件
     * @param folder   文件夹（如: avatars, posters）
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String folder) throws Exception {
        // 验证文件
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 验证文件类型（只允许图片）
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("只支持上传图片文件");
        }

        // 验证文件大小（限制5MB）
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("文件大小不能超过 5MB");
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;

        // 构建对象路径：folder/filename
        String objectName = folder + "/" + filename;

        try (InputStream inputStream = file.getInputStream()) {
            // 上传文件到 MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(contentType)
                            .build()
            );

            // 返回文件访问URL
            String fileUrl = minioConfig.getUrlPrefix() + "/" + objectName;
            log.info("文件上传成功: {}", fileUrl);
            return fileUrl;

        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     */
    public void deleteFile(String fileUrl) {
        try {
            // 从URL中提取对象名称
            // 例如: http://localhost:9000/movie-images/avatars/xxx.jpg -> avatars/xxx.jpg
            String objectName = fileUrl.replace(minioConfig.getUrlPrefix() + "/", "");

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .build()
            );
            log.info("文件删除成功: {}", objectName);
        } catch (Exception e) {
            log.error("文件删除失败: {}", fileUrl, e);
        }
    }

    /**
     * 上传用户头像
     */
    public String uploadAvatar(MultipartFile file) throws Exception {
        return uploadFile(file, "avatars");
    }

    /**
     * 上传电影海报
     */
    public String uploadPoster(MultipartFile file) throws Exception {
        return uploadFile(file, "posters");
    }

    /**
     * 上传演员/导演照片
     */
    public String uploadPersonPhoto(MultipartFile file) throws Exception {
        return uploadFile(file, "persons");
    }
}
