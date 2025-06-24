package com.movie_back.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonInfoDTO {
    private Long id;
    private String name;
    private String profileImageUrl;
}