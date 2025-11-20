package com.rafaelcabanillas.choirapi.dto;

import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class BlogPostDTO {
    private Long id;
    private String title;
    private Map<String, Object> content;

    // --- CHANGED: From String to Object ---
    private AuthorInfo author;
    // --------------------------------------

    private String imageUrl;
    private boolean isPublic;
    private int likes;
    private List<String> likesUsers;
    private List<CommentDTO> comments;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // Inner DTO to avoid exposing the full User password/email
    @Data
    @Builder
    public static class AuthorInfo {
        private Long id;
        private String name;     // Display Name ("Rafael Cabanillas")
        private String username; // Handle ("rafacaba")
        private String imageUrl; // Profile Pic
    }
}