package com.rafaelcabanillas.choirapi.repository;

import com.rafaelcabanillas.choirapi.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // No special methods needed, we always fetch by ID 1
}
