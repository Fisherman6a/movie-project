package com.movie_back.backend.controller;

import com.movie_back.backend.dto.movie.CreateMovieRequest;
import com.movie_back.backend.dto.movie.MovieDTO;
import com.movie_back.backend.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieDTO> createMovie(@Valid @RequestBody CreateMovieRequest movieRequest) {
        return new ResponseEntity<>(movieService.createMovie(movieRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable Long id,
            @Valid @RequestBody CreateMovieRequest movieRequest) {
        return ResponseEntity.ok(movieService.updateMovie(id, movieRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable Long id) {
        MovieDTO movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MovieDTO>> searchMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer releaseYear,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer yearStart,
            @RequestParam(required = false) Integer yearEnd,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<MovieDTO> movies = movieService.searchMovies(
                title, releaseYear, genre, country, minRating,
                yearStart, yearEnd,
                sortBy, sortDir, page, size);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/hot")
    public ResponseEntity<List<MovieDTO>> getHotMovies(@RequestParam(defaultValue = "10") int limit) {
        List<MovieDTO> hotMovies = movieService.getHotMovies(limit);
        return ResponseEntity.ok(hotMovies);
    }

    @GetMapping("/by-actor")
    public ResponseEntity<List<MovieDTO>> getMoviesByActorName(@RequestParam String name) {
        List<MovieDTO> movies = movieService.getMoviesByActorName(name);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/by-director")
    public ResponseEntity<List<MovieDTO>> getMoviesByDirectorName(@RequestParam String name) {
        List<MovieDTO> movies = movieService.getMoviesByDirectorName(name);
        return ResponseEntity.ok(movies);
    }
}