package com.movie_back.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "actors")
@Data
@EqualsAndHashCode(exclude = "moviesActedIn")
@ToString(exclude = "moviesActedIn")
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ActorID

    @Column(length = 100, nullable = false)
    private String name; // 姓名

    @Column(length = 20)
    private String gender; // 性别 (如: "Male", "Female", "Other")

    private LocalDate birthDate; // 出生日期

    @Column(length = 100)
    private String nationality; // 国籍

    @Column(length = 2048)
    private String profileImageUrl; // 演员图片URL

    @ManyToMany(mappedBy = "cast", fetch = FetchType.LAZY)
    private Set<Movie> moviesActedIn = new HashSet<>();
}