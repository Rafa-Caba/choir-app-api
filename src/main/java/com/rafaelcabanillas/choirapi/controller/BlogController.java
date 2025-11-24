package com.rafaelcabanillas.choirapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafaelcabanillas.choirapi.dto.BlogPostDTO;
import com.rafaelcabanillas.choirapi.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blog/posts")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;
    private final ObjectMapper objectMapper;

    // Public Read
    @GetMapping
    public ResponseEntity<List<BlogPostDTO>> getPublicPosts() {
        return ResponseEntity.ok(blogService.getAll(false));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogPostDTO> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(blogService.getById(id));
    }

    // Admin Read/Write
    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<List<BlogPostDTO>> getAdminPosts() {
        return ResponseEntity.ok(blogService.getAll(true));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<BlogPostDTO> create(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        BlogPostDTO dto = objectMapper.readValue(dataJson, BlogPostDTO.class);
        return ResponseEntity.ok(blogService.create(dto.getTitle(), dto.getContent(), dto.isPublic(), file));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<BlogPostDTO> update(
            @PathVariable Long id,
            @RequestPart("data") String dataJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        BlogPostDTO dto = objectMapper.readValue(dataJson, BlogPostDTO.class);
        return ResponseEntity.ok(blogService.update(id, dto.getTitle(), dto.getContent(), dto.isPublic(), file));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws IOException {
        blogService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Interactions
    @PostMapping("/{id}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BlogPostDTO> toggleLike(@PathVariable Long id) {
        return ResponseEntity.ok(blogService.toggleLike(id));
    }

    @PostMapping("/{id}/comment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BlogPostDTO> addComment(@PathVariable Long id, @RequestBody Map<String, Object> text) {
        return ResponseEntity.ok(blogService.addComment(id, text));
    }
}