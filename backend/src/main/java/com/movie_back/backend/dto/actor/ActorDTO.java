package com.movie_back.backend.dto.actor;

import com.movie_back.backend.entity.Gender; // 引入 Gender
import lombok.Data;
import java.time.LocalDate;

@Data
public class ActorDTO {
    private Long id;
    private String name;
    private Gender gender;
    private LocalDate birthDate;
    private String nationality;
    private String profileImageUrl;
    private String biography;
}