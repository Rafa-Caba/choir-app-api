package com.rafaelcabanillas.choirapi.dto;

import com.rafaelcabanillas.choirapi.model.MessageType;
import lombok.Data;
import java.util.Map;

@Data
public class NewMessageDTO {
    private String username; // Who sent it (since WS doesn't always send headers easily)
    private Map<String, Object> content;
    private MessageType type;

    // Optional: if they uploaded a file via REST first
    private String fileUrl;
    private String filename;
    private String imageUrl;
    private String imagePublicId;
    private String audioUrl;
    private String audioPublicId;

    private Long replyToId;
}