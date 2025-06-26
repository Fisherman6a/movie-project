package com.movie_back.backend.config;

import com.movie_back.backend.entity.Role;
import com.movie_back.backend.entity.User;
import com.movie_back.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // 如果 admin 用户已存在，则删除它
        userRepository.findByUsername("admin").ifPresent(userRepository::delete);

        // 创建一个管理员用户（如果不存在）
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@movie-time.com");
            admin.setRole(Role.ROLE_ADMIN);
            admin.setProfileImageUrl("https://i.ibb.co/0jFjF5bB/catcat.jpg");
            userRepository.save(admin);
            System.out.println("Created ADMIN user: admin");
        }

        // 如果 user 用户已存在，则删除它
        userRepository.findByUsername("user").ifPresent(userRepository::delete);

        // 创建一个普通用户（如果不存在）
        if (userRepository.findByUsername("user").isEmpty()) {
            User regularUser = new User();
            regularUser.setUsername("user");
            regularUser.setPassword(passwordEncoder.encode("user123"));
            regularUser.setEmail("user@movie-time.com");
            regularUser.setRole(Role.ROLE_USER);
            regularUser.setProfileImageUrl("https://i.ibb.co/9kR8CgCw/catcat.jpg");
            userRepository.save(regularUser);
            System.out.println("Created USER user: user");
        }
    }
}