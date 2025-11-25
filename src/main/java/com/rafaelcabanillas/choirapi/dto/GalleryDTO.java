package com.rafaelcabanillas.choirapi.dto;

import com.rafaelcabanillas.choirapi.model.MediaType;
import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@Builder
public class GalleryDTO {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private MediaType mediaType;
    private boolean imageStart;
    private boolean imageTopBar;
    private boolean imageUs;
    private boolean imageLogo;
    private boolean imageGallery;
    private OffsetDateTime createdAt;
}