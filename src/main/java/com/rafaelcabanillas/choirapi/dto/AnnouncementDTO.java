package com.rafaelcabanillas.choirapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementDTO {
    private Long id;
    private String title;
    private Map<String, Object> content; // Rich Text
    private String imageUrl;

    @JsonProperty("isPublic")
    private boolean isPublic;

    private OffsetDateTime createdAt;
}