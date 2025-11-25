package com.rafaelcabanillas.choirapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafaelcabanillas.choirapi.dto.SongDTO;
import com.rafaelcabanillas.choirapi.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<SongDTO>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongs());
    }

    // Use String wrapper + MultipartFile for Audio
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<SongDTO> createSong(
            @RequestPart("data") String songJson,
            @RequestPart(value = "file", required = false) MultipartFile audioFile
    ) throws IOException {
        SongDTO dto = objectMapper.readValue(songJson, SongDTO.class);
        return ResponseEntity.ok(songService.createSong(dto, audioFile));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<SongDTO> updateSong(
            @PathVariable Long id,
            @RequestPart("data") String songJson,
            @RequestPart(value = "file", required = false) MultipartFile audioFile
    ) throws IOException {
        SongDTO dto = objectMapper.readValue(songJson, SongDTO.class);
        return ResponseEntity.ok(songService.updateSong(id, dto, audioFile));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) throws IOException {
        songService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }
}