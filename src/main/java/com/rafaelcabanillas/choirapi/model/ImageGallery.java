package com.rafaelcabanillas.choirapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "gallery_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageGallery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Column(nullable = false)
    private String imageUrl;
    private String imagePublicId;

    // --- NEW FIELD ---
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MediaType mediaType = MediaType.IMAGE;

    // --- Feature Flags ---
    @Builder.Default private boolean imageStart = false;
    @Builder.Default private boolean imageTopBar = false;
    @Builder.Default private boolean imageUs = false;
    @Builder.Default private boolean imageLogo = false;
    @Builder.Default private boolean imageGallery = true;

    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}