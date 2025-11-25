package com.rafaelcabanillas.choirapi.controller;

import com.rafaelcabanillas.choirapi.dto.SongTypeDTO;
import com.rafaelcabanillas.choirapi.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/song-types")
@RequiredArgsConstructor
public class SongTypeController {

    private final SongService songService;

    @GetMapping
    public ResponseEntity<List<SongTypeDTO>> getSongTypes() {
        return ResponseEntity.ok(songService.getAllTypes());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SongTypeDTO> createType(@RequestBody SongTypeDTO dto) {
        return ResponseEntity.ok(songService.createType(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SongTypeDTO> updateType(@PathVariable Long id, @RequestBody SongTypeDTO dto) {
        return ResponseEntity.ok(songService.updateType(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteType(@PathVariable Long id) {
        songService.deleteType(id);
        return ResponseEntity.noContent().build();
    }
}