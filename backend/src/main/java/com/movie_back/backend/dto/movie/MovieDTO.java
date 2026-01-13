package com.movie_back.backend.dto.movie;

import lombok.Data;

import java.util.List;

import com.movie_back.backend.dto.PersonInfoDTO;

@Data
public class MovieDTO {
    private Long id;
    private String title;
    private Integer releaseYear;
    private Integer duration;
    private String genre;
    private String language;
    private String country;
    private String synopsis;
    private Double averageRating;
    private String posterUrl;
    private List<PersonInfoDTO> cast;
    private List<PersonInfoDTO> directors; 
    private List<RatingDistributionDTO> ratingDistribution;
}