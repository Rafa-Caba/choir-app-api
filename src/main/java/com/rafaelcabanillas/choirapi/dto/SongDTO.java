package com.rafaelcabanillas.choirapi.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class SongDTO {
    private Long id;
    private String title;
    private String composer;

    // This Map will hold your entire Tiptap/ProseMirror JSON structure
    private Map<String, Object> content;

    private Long songTypeId;
    private String songTypeName;
}