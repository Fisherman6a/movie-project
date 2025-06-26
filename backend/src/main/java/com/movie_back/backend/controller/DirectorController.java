package com.movie_back.backend.controller;

import com.movie_back.backend.dto.director.DirectorDTO;
import com.movie_back.backend.service.DirectorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DirectorDTO> createDirector(@Valid @RequestBody DirectorDTO directorDTO) {
        return new ResponseEntity<>(directorService.createDirector(directorDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DirectorDTO>> getAllDirectors() {
        return ResponseEntity.ok(directorService.getAllDirectors());
    }

    @GetMapping("/search")
    public ResponseEntity<List<DirectorDTO>> searchDirectors(@RequestParam String name) {
        return ResponseEntity.ok(directorService.searchDirectorsByName(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DirectorDTO> getDirectorById(@PathVariable Long id) {
        return ResponseEntity.ok(directorService.getDirectorById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DirectorDTO> updateDirector(@PathVariable Long id,
            @Valid @RequestBody DirectorDTO directorDTO) {
        return ResponseEntity.ok(directorService.updateDirector(id, directorDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDirector(@PathVariable Long id) {
        directorService.deleteDirector(id);
        return ResponseEntity.noContent().build();
    }
}