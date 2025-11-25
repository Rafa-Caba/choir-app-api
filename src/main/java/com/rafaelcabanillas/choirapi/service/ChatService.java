package com.rafaelcabanillas.choirapi.service;

import com.rafaelcabanillas.choirapi.dto.ChatMessageDTO;
import com.rafaelcabanillas.choirapi.dto.NewMessageDTO;
import com.rafaelcabanillas.choirapi.dto.UserDTO;
import com.rafaelcabanillas.choirapi.model.ChatMessage;
import com.rafaelcabanillas.choirapi.model.User;
import com.rafaelcabanillas.choirapi.repository.ChatMessageRepository;
import com.rafaelcabanillas.choirapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatRepository;
    private final UserRepository userRepository;

    // Get last 50 messages for history
    public List<ChatMessageDTO> getHistory() {
        // We fetch the latest 50, but they come in reverse order (newest first).
        // You usually reverse them in the frontend or here to show chronological order.
        return chatRepository.findAll(PageRequest.of(0, 50, Sort.by("createdAt").descending()))
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatMessageDTO saveMessage(NewMessageDTO dto) {
        User author = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChatMessage parent = null;
        if (dto.getReplyToId() != null) {
            parent = chatRepository.findById(dto.getReplyToId()).orElse(null);
        }

        ChatMessage message = ChatMessage.builder()
                .author(author)
                .content(dto.getContent())
                .replyTo(parent)
                .type(dto.getType())
                .fileUrl(dto.getFileUrl())
                .filename(dto.getFilename())
                .imageUrl(dto.getImageUrl())
                .imagePublicId(dto.getImagePublicId())
                .audioUrl(dto.getAudioUrl())
                .audioPublicId(dto.getAudioPublicId())
                .build();

        return toDTO(chatRepository.save(message));
    }

    private ChatMessageDTO toDTO(ChatMessage msg) {
        ChatMessageDTO.ReplyPreviewDTO replyPreview = null;
        if (msg.getReplyTo() != null) {
            // Helper to extract text from the JSON Map (Simplified logic)
            String rawText = "Multimedia/Rich Text";
            try {
                // Assuming standard Tiptap structure: content[0].content[0].text
                // You can make this robust based on your JSON structure
                List<Map<String, Object>> contentList = (List) msg.getReplyTo().getContent().get("content");
                if (contentList != null && !contentList.isEmpty()) {
                    List<Map<String, Object>> innerList = (List) contentList.get(0).get("content");
                    if (innerList != null && !innerList.isEmpty()) {
                        rawText = (String) innerList.get(0).get("text");
                    }
                }
            } catch (Exception e) { /* Ignore parse error */ }

            replyPreview = ChatMessageDTO.ReplyPreviewDTO.builder()
                    .id(msg.getReplyTo().getId())
                    .username(msg.getReplyTo().getAuthor().getName())
                    .textPreview(rawText)
                    .build();
        }

        return ChatMessageDTO.builder()
                .id(msg.getId())
                .author(UserDTO.fromEntity(msg.getAuthor()))
                .content(msg.getContent())
                .type(msg.getType())
                .fileUrl(msg.getFileUrl())
                .filename(msg.getFilename())
                .imageUrl(msg.getImageUrl())
                .audioUrl(msg.getAudioUrl())
                .createdAt(msg.getCreatedAt())
                .reactions(msg.getReactions().stream()
                        .map(r -> ChatMessageDTO.ReactionDTO.builder()
                                .emoji(r.getEmoji())
                                .username(r.getUsername())
                                .build())
                        .collect(Collectors.toList()))
                .replyTo(replyPreview)
                .build();
    }
}