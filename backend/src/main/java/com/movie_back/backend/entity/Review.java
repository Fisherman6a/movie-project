package com.movie_back.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
// 增加表注解，并定义 user_id 和 movie_id 的联合唯一约束
@Table(name = "reviews", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "movie_id" })
})
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer score; // 1-10 分制

    @Column(nullable = false)
    private Integer likes = 0;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String commentText;

    private LocalDateTime createdAt;

    // 一对多关系：一条评论可以有多个点赞
    // 注意：不使用 cascade，手动在 Service 层控制删除顺序，避免 Hibernate 懒加载并发修改异常
    @OneToMany(mappedBy = "review", fetch = FetchType.LAZY)
    private Set<ReviewLike> reviewLikes = new LinkedHashSet<>();
    // private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        // updatedAt = LocalDateTime.now();
    }

    // @PreUpdate
    // protected void onUpdate() {
    // updatedAt = LocalDateTime.now();
    // }
}