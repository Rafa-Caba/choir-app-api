package com.rafaelcabanillas.choirapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    // Rich Text Content
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> content;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    // File attachments
    private String fileUrl;
    private String filename;
    private String imageUrl;
    private String imagePublicId;

    // Reactions (e.g., "👍" from "rafacaba")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "chat_reactions", joinColumns = @JoinColumn(name = "message_id"))
    @Builder.Default
    private List<MessageReaction> reactions = new ArrayList<>();

    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    // --- Inner Class for Reactions ---
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MessageReaction {
        private String emoji;
        private String username; // Storing username is simpler than full relation for reactions
    }

    @ManyToOne
    @JoinColumn(name = "reply_to_id")
    private ChatMessage replyTo;
}