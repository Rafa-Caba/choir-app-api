package com.rafaelcabanillas.choirapi.controller;

import com.rafaelcabanillas.choirapi.dto.GalleryDTO;
import com.rafaelcabanillas.choirapi.service.GalleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/gallery")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;

    @GetMapping
    public ResponseEntity<List<GalleryDTO>> getAllImages() {
        return ResponseEntity.ok(galleryService.getAllImages());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<GalleryDTO> uploadImage(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("image") MultipartFile file,
            @RequestParam(value = "imageStart", defaultValue = "false") boolean start,
            @RequestParam(value = "imageTopBar", defaultValue = "false") boolean topBar,
            @RequestParam(value = "imageUs", defaultValue = "false") boolean us,
            @RequestParam(value = "imageLogo", defaultValue = "false") boolean logo,
            @RequestParam(value = "imageGallery", defaultValue = "true") boolean gallery
    ) throws IOException {
        return ResponseEntity.ok(galleryService.uploadImage(title, description, file, start, topBar, us, logo, gallery));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) throws IOException {
        galleryService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}