package com.rafaelcabanillas.choirapi.controller;

import com.rafaelcabanillas.choirapi.dto.ChatMessageDTO;
import com.rafaelcabanillas.choirapi.dto.NewMessageDTO;
import com.rafaelcabanillas.choirapi.service.ChatService;
import com.rafaelcabanillas.choirapi.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final CloudinaryService cloudinaryService;

    // --- REST Endpoints (HTTP) ---

    @GetMapping("/api/chat/history")
    public ResponseEntity<List<ChatMessageDTO>> getHistory() {
        return ResponseEntity.ok(chatService.getHistory());
    }

    // Special endpoint for uploading chat images
    @PostMapping("/api/chat/upload")
    public ResponseEntity<Map<String, String>> uploadMedia(@RequestParam("file") MultipartFile file) throws IOException {
        Map result = cloudinaryService.uploadFile(file, "choir/chat");
        return ResponseEntity.ok(Map.of(
                "url", (String) result.get("secure_url"),
                "publicId", (String) result.get("public_id")
        ));
    }

    // --- WebSocket Endpoints (Real-Time) ---

    @MessageMapping("/chat.sendMessage") // Client sends to /app/chat.sendMessage
    @SendTo("/topic/public")             // Server pushes to /topic/public
    public ChatMessageDTO sendMessage(@Payload NewMessageDTO chatMessage) {
        // 1. Save to DB
        return chatService.saveMessage(chatMessage);
        // 2. Return value is automatically pushed to all subscribers of /topic/public
    }
}