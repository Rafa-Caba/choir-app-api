package com.rafaelcabanillas.choirapi.dto;

import com.rafaelcabanillas.choirapi.model.MessageType;
import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ChatMessageDTO {
    private Long id;
    private UserDTO author; // Re-use your UserDTO
    private Map<String, Object> content;
    private MessageType type;
    private String fileUrl;
    private String filename;
    private String imageUrl;
    private List<ReactionDTO> reactions;
    private OffsetDateTime createdAt;
    private ReplyPreviewDTO replyTo;

    @Data
    @Builder
    public static class ReactionDTO {
        private String emoji;
        private String username;
    }

    @Data
    @Builder
    public static class ReplyPreviewDTO {
        private Long id;
        private String username;
        private String textPreview;
    }
}