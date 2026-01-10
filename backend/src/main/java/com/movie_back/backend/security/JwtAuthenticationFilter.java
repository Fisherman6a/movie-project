package com.movie_back.backend.security;

import com.movie_back.backend.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j // 添加日志支持
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final TokenService tokenService; // 新增：Redis Token 管理

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        log.debug("请求路径: {}", request.getRequestURI());

        // 如果请求头中没有 "Authorization" 或不以 "Bearer " 开头，则直接放行
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("没有找到 Authorization header 或格式不正确");
            filterChain.doFilter(request, response);
            return;
        }

        // 提取JWT
        jwt = authHeader.substring(7);
        log.debug("提取到 JWT token: {}...", jwt.substring(0, Math.min(20, jwt.length())));

        try {
            username = jwtUtil.extractUsername(jwt);
            log.debug("从 token 中提取到用户名: {}", username);
        } catch (Exception e) {
            // 如果JWT解析失败，则直接放行，后续的Security链会处理未认证状态
            log.error("JWT 解析失败: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // 如果用户名不为空，并且当前安全上下文中没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 检查 token 是否在黑名单中
            boolean isValid = tokenService.isTokenValid(jwt);
            log.debug("Token 黑名单检查结果: {}", isValid ? "有效（未在黑名单中）" : "无效（在黑名单中）");

            if (!isValid) {
                // Token 无效（已登出），拒绝请求
                log.warn("Token 在黑名单中，拒绝访问");
                filterChain.doFilter(request, response);
                return;
            }

            // 从数据库加载用户信息
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            log.debug("加载到用户信息: {}", userDetails.getUsername());

            // 验证token是否有效
            boolean tokenValid = jwtUtil.validateToken(jwt, (com.movie_back.backend.entity.User) userDetails);
            log.debug("JWT 验证结果: {}", tokenValid);

            if (tokenValid) {
                // 创建一个认证令牌
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 将认证令牌设置到安全上下文中
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("认证成功，用户: {}", username);
            } else {
                log.warn("JWT 验证失败，用户: {}", username);
            }
        }
        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
}