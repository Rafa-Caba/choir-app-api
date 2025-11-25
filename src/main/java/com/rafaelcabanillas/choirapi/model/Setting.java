package com.rafaelcabanillas.choirapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Table(name = "settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Setting {

    @Id
    private Long id; // We will manually set this to 1 in the migration

    @Column(nullable = false)
    private String appTitle;

    // Embeddable for Social Links
    @Embedded
    @Builder.Default
    private SocialLinks socialLinks = new SocialLinks();

    // Rich Text for "About App"
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> aboutApp;

    private String appLogoUrl;
    private String appLogoPublicId;

    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    // --- Inner Class for Social Links ---
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SocialLinks {
        private String facebook;
        private String instagram;
        private String youtube;
        private String whatsapp;
    }
}