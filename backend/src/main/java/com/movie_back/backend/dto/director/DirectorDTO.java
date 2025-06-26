package com.movie_back.backend.dto.director;

import com.movie_back.backend.entity.Gender;
import lombok.Data;
import java.time.LocalDate;

@Data
public class DirectorDTO {
    private Long id;
    private String name;
    private Gender gender; 
    private LocalDate birthDate;
    private String nationality;
    private String profileImageUrl;
    private String biography;
}