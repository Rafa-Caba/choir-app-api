package com.rafaelcabanillas.choirapi.controller;

import com.rafaelcabanillas.choirapi.dto.SongDTO;
import com.rafaelcabanillas.choirapi.dto.SongTypeDTO;
import com.rafaelcabanillas.choirapi.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @GetMapping("/song-types")
    public ResponseEntity<List<SongTypeDTO>> getSongTypes() {
        return ResponseEntity.ok(songService.getAllTypes());
    }

    @GetMapping("/songs")
    public ResponseEntity<List<SongDTO>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongs());
    }

    @PostMapping("/songs")
    public ResponseEntity<SongDTO> createSong(@RequestBody SongDTO dto) {
        return ResponseEntity.ok(songService.createSong(dto));
    }

    @PutMapping("/songs/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<SongDTO> updateSong(@PathVariable Long id, @RequestBody SongDTO dto) {
        return ResponseEntity.ok(songService.updateSong(id, dto));
    }
}