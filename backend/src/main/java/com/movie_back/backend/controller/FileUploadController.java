package com.movie_back.backend.controller;

import com.movie_back.backend.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 * 处理用户头像、电影海报等图片上传
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * 上传用户头像
     * POST /api/upload/avatar
     */
    @PostMapping("/avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        try {
            String fileUrl = fileUploadService.uploadAvatar(file);
            response.put("status", "success");
            response.put("url", fileUrl);
            response.put("message", "头像上传成功");
            log.info("用户头像上传成功: {}", fileUrl);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("头像上传失败", e);
            response.put("status", "error");
            response.put("message", "上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 上传电影海报
     * POST /api/upload/poster
     * 需要管理员权限
     */
    @PostMapping("/poster")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> uploadPoster(@RequestParam("file") MultipartFile file) {
        log.info("===== 开始处理海报上传 =====");
        log.info("文件名: {}, 大小: {} bytes", file.getOriginalFilename(), file.getSize());

        Map<String, String> response = new HashMap<>();
        try {
            String fileUrl = fileUploadService.uploadPoster(file);
            response.put("status", "success");
            response.put("url", fileUrl);
            response.put("message", "海报上传成功");
            log.info("电影海报上传成功: {}", fileUrl);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("海报上传失败", e);
            response.put("status", "error");
            response.put("message", "上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 上传演员/导演照片
     * POST /api/upload/person
     * 需要管理员权限
     */
    @PostMapping("/person")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> uploadPersonPhoto(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        try {
            String fileUrl = fileUploadService.uploadPersonPhoto(file);
            response.put("status", "success");
            response.put("url", fileUrl);
            response.put("message", "照片上传成功");
            log.info("演员/导演照片上传成功: {}", fileUrl);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("照片上传失败", e);
            response.put("status", "error");
            response.put("message", "上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 通用文件上传接口
     * POST /api/upload/file
     * 需要认证
     */
    @PostMapping("/file")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {
        Map<String, String> response = new HashMap<>();
        try {
            String fileUrl = fileUploadService.uploadFile(file, folder);
            response.put("status", "success");
            response.put("url", fileUrl);
            response.put("message", "文件上传成功");
            log.info("文件上传成功: folder={}, url={}", folder, fileUrl);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            response.put("status", "error");
            response.put("message", "上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
