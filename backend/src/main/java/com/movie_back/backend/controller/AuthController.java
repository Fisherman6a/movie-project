package com.movie_back.backend.controller;

import com.movie_back.backend.dto.auth.AuthRequest;
import com.movie_back.backend.dto.auth.AuthResponse;
import com.movie_back.backend.dto.user.UserDTO;
import com.movie_back.backend.dto.user.UserRegistrationRequest;
import com.movie_back.backend.entity.User;
import com.movie_back.backend.security.JwtUtil;
import com.movie_back.backend.service.CaptchaService;
import com.movie_back.backend.service.TokenService;
import com.movie_back.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService; // Redis Token 管理
    private final CaptchaService captchaService; // 图形验证码服务

    @Value("${jwt.expiration}")
    private long jwtExpiration; // JWT 过期时间

    // 用户登录接口
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        // 1. 验证图形验证码
        if (!captchaService.verifyCaptcha(request.getCaptchaId(), request.getCaptcha())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "验证码错误或已过期"));
        }

        // 2. 使用 AuthenticationManager 进行用户认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        // 3. 认证成功后，获取用户信息
        User user = (User) authentication.getPrincipal();

        // 4. 生成JWT
        String token = jwtUtil.generateToken(user);

        // 5. 保存 token 到 Redis
        tokenService.saveToken(token, user.getId(), jwtExpiration);

        // 6. 返回JWT和用户信息
        AuthResponse response = new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                user.getProfileImageUrl(),
                user.getEmail(),
                user.getPersonalWebsite(),
                user.getBirthDate(),
                user.getBio(),
                user.getCreatedAt());
        return ResponseEntity.ok(response);
    }

    // 用户注册接口
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserRegistrationRequest request) {
        UserDTO newUser = userService.registerUser(request);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    // 新增：用户登出接口
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        // 从请求头中提取 token
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // 从 Redis 删除 token
            tokenService.deleteToken(token);
            // 加入黑名单，双重保证
            tokenService.addToBlacklist(token, jwtExpiration);
        }
        return ResponseEntity.ok("Logout successful");
    }
}