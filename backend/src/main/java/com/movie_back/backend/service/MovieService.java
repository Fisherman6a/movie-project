package com.movie_back.backend.service;

import com.movie_back.backend.dto.PersonInfoDTO;
import com.movie_back.backend.dto.movie.CreateMovieRequest;
import com.movie_back.backend.dto.movie.MovieDTO;
import com.movie_back.backend.dto.movie.RatingDistributionDTO;
import com.movie_back.backend.entity.Actor;
import com.movie_back.backend.entity.Director;
import com.movie_back.backend.entity.Movie;
import com.movie_back.backend.entity.UserRating;
import com.movie_back.backend.exception.ResourceNotFoundException;
import com.movie_back.backend.repository.ActorRepository;
import com.movie_back.backend.repository.DirectorRepository;
import com.movie_back.backend.repository.MovieRepository;
import com.movie_back.backend.repository.UserRatingRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;
    private final DirectorRepository directorRepository;
    private final UserRatingRepository userRatingRepository;

    @Transactional
    public MovieDTO createMovie(CreateMovieRequest request) {
        Movie movie = new Movie();
        setMoviePropertiesFromRequest(movie, request);
        Movie savedMovie = movieRepository.save(movie);
        return convertToMovieDTO(savedMovie);
    }

    @Transactional(readOnly = true)
    public MovieDTO getMovieById(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));
        movie.getCast().size();
        movie.getDirectors().size();
        return convertToMovieDTO(movie);
    }

    @Transactional
    public MovieDTO updateMovie(Long movieId, CreateMovieRequest request) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));
        setMoviePropertiesFromRequest(movie, request);
        Movie updatedMovie = movieRepository.save(movie);
        return convertToMovieDTO(updatedMovie);
    }

    @Transactional
    public void deleteMovie(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new ResourceNotFoundException("Movie not found with id: " + movieId);
        }
        movieRepository.deleteById(movieId);
    }

    @Transactional
    public void updateMovieAverageRating(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));
        double average = movie.getUserRatings().stream()
                .mapToInt(UserRating::getScore)
                .average()
                .orElse(0.0);
        movie.setAverageRating(Math.round(average * 10.0) / 10.0);
        movieRepository.save(movie);
    }

    @Transactional(readOnly = true)
    public Page<MovieDTO> searchMovies(
            String title, // **新增**
            Integer releaseYear,
            String genre, String country, Double minRating,
            String sortBy,
            String sortDir, int page, int size) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Specification<Movie> spec = Specification.where(null);

        // **新增**：按标题模糊查询的逻辑
        if (title != null && !title.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }

        if (releaseYear != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("releaseYear"), releaseYear));
        }
        if (genre != null && !genre.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("genre")), "%" + genre.toLowerCase() + "%"));
        }
        if (country != null && !country.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("country")), country.toLowerCase()));
        }
        if (minRating != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("averageRating"), minRating));
        }

        Page<Movie> moviePage = movieRepository.findAll(spec, pageable);

        moviePage.getContent().forEach(movie -> {
            movie.getCast().size();
            movie.getDirectors().size();
        });

        return moviePage.map(this::convertToMovieDTO);
    }

    @Transactional(readOnly = true)
    public List<MovieDTO> getHotMovies(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "averageRating"));
        List<Movie> movies = movieRepository.findAll(pageable).getContent();
        movies.forEach(movie -> {
            movie.getCast().size();
            movie.getDirectors().size();
        });
        return movies.stream()
                .map(this::convertToMovieDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovieDTO> getMoviesByActorName(String actorName) {
        List<Movie> movies = movieRepository.findMoviesByActorNameContaining(actorName);
        movies.forEach(movie -> {
            movie.getCast().size();
            movie.getDirectors().size();
        });
        return movies.stream()
                .map(this::convertToMovieDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovieDTO> getMoviesByDirectorName(String directorName) {
        List<Movie> movies = movieRepository.findMoviesByDirectorNameContaining(directorName);
        movies.forEach(movie -> {
            movie.getCast().size();
            movie.getDirectors().size();
        });
        return movies.stream()
                .map(this::convertToMovieDTO)
                .collect(Collectors.toList());
    }

    private void setMoviePropertiesFromRequest(Movie movie, CreateMovieRequest request) {
        movie.setTitle(request.getTitle());
        movie.setReleaseYear(request.getReleaseYear());
        movie.setDuration(request.getDuration());
        movie.setGenre(request.getGenre());
        movie.setLanguage(request.getLanguage());
        movie.setCountry(request.getCountry());
        movie.setSynopsis(request.getSynopsis());
        movie.setPosterUrl(request.getPosterUrl());

        if (request.getActorIds() != null && !request.getActorIds().isEmpty()) {
            List<Actor> actors = actorRepository.findAllById(request.getActorIds());
            movie.setCast(new HashSet<>(actors));
        } else {
            movie.getCast().clear();
        }

        if (request.getDirectorIds() != null && !request.getDirectorIds().isEmpty()) {
            List<Director> directors = directorRepository.findAllById(request.getDirectorIds());
            movie.setDirectors(new HashSet<>(directors));
        } else {
            movie.getDirectors().clear();
        }
    }

    private MovieDTO convertToMovieDTO(Movie movie) {
        MovieDTO dto = new MovieDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setReleaseYear(movie.getReleaseYear());
        dto.setDuration(movie.getDuration());
        dto.setGenre(movie.getGenre());
        dto.setLanguage(movie.getLanguage());
        dto.setCountry(movie.getCountry());
        dto.setSynopsis(movie.getSynopsis());
        dto.setAverageRating(movie.getAverageRating());
        dto.setPosterUrl(movie.getPosterUrl());

        if (movie.getCast() != null) {
            dto.setCast(movie.getCast().stream()
                    .map(actor -> new PersonInfoDTO(actor.getId(), actor.getName(), actor.getProfileImageUrl()))
                    .collect(Collectors.toList()));
        }
        if (movie.getDirectors() != null) {
            dto.setDirectors(movie.getDirectors().stream()
                    .map(director -> new PersonInfoDTO(director.getId(), director.getName(),
                            director.getProfileImageUrl()))
                    .collect(Collectors.toList()));
        }
        List<RatingDistributionDTO> distribution = userRatingRepository.getRatingDistributionForMovie(movie.getId());
        long totalRatings = distribution.stream().mapToLong(RatingDistributionDTO::getCount).sum();

        if (totalRatings > 0) {
            for (RatingDistributionDTO dist : distribution) {
                dist.setPercentage(Math.round((double) dist.getCount() / totalRatings * 1000) / 10.0);
            }
        }
        dto.setRatingDistribution(distribution);
        return dto;
    }
}