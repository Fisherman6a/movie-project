package com.movie_back.backend.service;

import com.movie_back.backend.dto.user.UserDTO;
import com.movie_back.backend.dto.user.UserProfileUpdateDTO;
import com.movie_back.backend.dto.user.UserRegistrationRequest;
import com.movie_back.backend.entity.Notification;
import com.movie_back.backend.entity.Role;
import com.movie_back.backend.entity.User;
import com.movie_back.backend.exception.ResourceNotFoundException;
import com.movie_back.backend.repository.NotificationRepository;
import com.movie_back.backend.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

// import lombok.RequiredArgsConstructor; // 移除这行
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationRepository notificationRepository;

    // 手动创建构造函数，并对PasswordEncoder使用@Lazy注解
    public UserService(UserRepository userRepository,
                       @Lazy PasswordEncoder passwordEncoder,
                       NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationRepository = notificationRepository;
    }

    // Spring Security 会调用此方法来加载用户信息
    @Override
    @Transactional(readOnly = true)
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    // 用户注册
    @Transactional
    public UserDTO registerUser(UserRegistrationRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalStateException("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already in use");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // 默认注册为普通用户
        user.setRole(Role.ROLE_USER);
        user.setProfileImageUrl("https://i.ibb.co/N23MW2Gp/userdefault.jpg");

        User savedUser = userRepository.save(user);

        // 发送欢迎通知
        Notification welcomeNotification = Notification.builder()
            .userId(savedUser.getId())
            .type(Notification.NotificationType.SYSTEM)
            .title("欢迎加入")
            .content("欢迎来到电影评论平台！开始探索和分享您对电影的见解吧。")
            .relatedId(null)
            .relatedType(null)
            .triggerUserId(null)
            .triggerUsername("系统")
            .build();

        notificationRepository.save(welcomeNotification);

        return convertToDTO(savedUser);
    }

    // 根据用户名查找用户并转换为DTO
    @Transactional(readOnly = true)
    public UserDTO findUserByUsername(String username) {
        User user = loadUserByUsername(username);
        return convertToDTO(user);
    }

    // 根据ID查找用户并转换为DTO
    @Transactional(readOnly = true)
    public UserDTO findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToDTO(user);
    }

    // 添加一个更新用户个人资料的方法
    @Transactional
    public UserDTO updateUserProfile(Long userId, UserProfileUpdateDTO profileData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setUsername(profileData.getUsername());
        user.setPersonalWebsite(profileData.getPersonalWebsite());
        user.setBirthDate(profileData.getBirthDate());
        user.setBio(profileData.getBio());

        // 如果请求中包含 profileImageUrl，则更新它
        if (profileData.getProfileImageUrl() != null && !profileData.getProfileImageUrl().isEmpty()) {
            user.setProfileImageUrl(profileData.getProfileImageUrl());
        }

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser); // convertToDTO 也需要更新以包含新字段
    }

    // 用户更新密码
    @Transactional
    public void changeUserPassword(User currentUser, String oldPassword, String newPassword) {
        // 1. 验证旧密码是否正确
        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            // 在实际应用中，可以定义一个更具体的异常类型
            throw new IllegalStateException("旧密码不正确");
        }
        // 2. 将新密码加密
        String encodedNewPassword = passwordEncoder.encode(newPassword);

        // 3. 更新用户密码
        currentUser.setPassword(encodedNewPassword);

        // 4. 保存到数据库
        userRepository.save(currentUser);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        // 防止管理员删除自己
        // (更复杂的逻辑可以检查当前登录的管理员ID是否与要删除的ID相同)
        if (user.getRole() == Role.ROLE_ADMIN) {
            throw new IllegalStateException("Cannot delete an admin account.");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public void changeUserEmail(User currentUser, String newEmail) {
        // 检查新邮箱是否已被其他用户占用
        userRepository.findByEmail(newEmail).ifPresent(user -> {
            if (!user.getId().equals(currentUser.getId())) {
                throw new IllegalStateException("该邮箱已被其他用户绑定");
            }
        });
        currentUser.setEmail(newEmail);
        userRepository.save(currentUser);
    }

    @Transactional
    public void changeUserPhone(User currentUser, String newPhone) {
        userRepository.findByPhone(newPhone).ifPresent(user -> {
            if (!user.getId().equals(currentUser.getId())) {
                throw new IllegalStateException("该电话号码已被其他用户绑定");
            }
        });
        currentUser.setPhone(newPhone);
        userRepository.save(currentUser);
    }
}