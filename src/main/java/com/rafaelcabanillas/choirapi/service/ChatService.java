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

import java.util.List;
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

        ChatMessage message = ChatMessage.builder()
                .author(author)
                .content(dto.getContent())
                .type(dto.getType())
                .fileUrl(dto.getFileUrl())
                .filename(dto.getFilename())
                .imageUrl(dto.getImageUrl())
                .imagePublicId(dto.getImagePublicId())
                .build();

        return toDTO(chatRepository.save(message));
    }

    private ChatMessageDTO toDTO(ChatMessage msg) {
        return ChatMessageDTO.builder()
                .id(msg.getId())
                .author(UserDTO.fromEntity(msg.getAuthor()))
                .content(msg.getContent())
                .type(msg.getType())
                .fileUrl(msg.getFileUrl())
                .filename(msg.getFilename())
                .imageUrl(msg.getImageUrl())
                .createdAt(msg.getCreatedAt())
                .reactions(msg.getReactions().stream()
                        .map(r -> ChatMessageDTO.ReactionDTO.builder()
                                .emoji(r.getEmoji())
                                .username(r.getUsername())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}