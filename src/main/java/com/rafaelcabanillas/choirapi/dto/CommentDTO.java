package com.rafaelcabanillas.choirapi.dto;

import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
public class CommentDTO {
    private String author;
    private Map<String, Object> text; // Rich text JSON
    private OffsetDateTime date;
}