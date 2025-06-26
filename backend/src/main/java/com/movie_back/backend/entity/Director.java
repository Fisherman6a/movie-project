package com.movie_back.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "directors")
@Data
@EqualsAndHashCode(exclude = "moviesDirected")
@ToString(exclude = "moviesDirected")
public class Director {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender;

    private LocalDate birthDate;

    @Column(length = 100)
    private String nationality;

    @Column(length = 2048)
    private String profileImageUrl;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @ManyToMany(mappedBy = "directors", fetch = FetchType.LAZY)
    private Set<Movie> moviesDirected = new HashSet<>();
}