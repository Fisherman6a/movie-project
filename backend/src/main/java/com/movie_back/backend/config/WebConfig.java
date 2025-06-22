package com.movie_back.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 从 application.properties 文件中注入我们定义的路径
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将所有以 /images/ 开头的请求 映射到 我们配置的本地文件目录
        // "file:" 这个前缀是必须的，它告诉 Spring Boot 这是一个本地文件系统路径
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadDir);
    }
}