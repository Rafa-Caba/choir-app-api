package com.rafaelcabanillas.choirapi.controller;

import com.rafaelcabanillas.choirapi.dto.AnnouncementDTO;
import com.rafaelcabanillas.choirapi.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService service;

    @GetMapping
    public ResponseEntity<List<AnnouncementDTO>> getPublic() {
        return ResponseEntity.ok(service.getAll(false));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<List<AnnouncementDTO>> getAdmin() {
        return ResponseEntity.ok(service.getAll(true));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<AnnouncementDTO> create(
            @RequestPart("data") AnnouncementDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(service.create(dto.getTitle(), dto.getContent(), dto.isPublic(), file));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<AnnouncementDTO> update(
            @PathVariable Long id,
            @RequestPart("data") AnnouncementDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(service.update(id, dto.getTitle(), dto.getContent(), dto.isPublic(), file));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws IOException {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}