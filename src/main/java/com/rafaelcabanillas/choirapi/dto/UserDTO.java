package com.rafaelcabanillas.choirapi.dto;

import com.rafaelcabanillas.choirapi.model.User;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String name;
    private String username;
    private String email;
    private String role;
    private String instrument;
    private boolean voice;
    private String bio;
    private String imageUrl;
    private String imagePublicId;
    private Long themeId; // Just the ID is enough for the frontend store
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // Helper method to convert Entity -> DTO
    public static UserDTO fromEntity(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .instrument(user.getInstrument())
                .voice(user.isVoice())
                .bio(user.getBio())
                .imageUrl(user.getImageUrl())
                .imagePublicId(user.getImagePublicId())
                .themeId(user.getTheme() != null ? user.getTheme().getId() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}