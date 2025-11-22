package com.rafaelcabanillas.choirapi.controller;

import com.rafaelcabanillas.choirapi.dto.ChatMessageDTO;
import com.rafaelcabanillas.choirapi.dto.NewMessageDTO;
import com.rafaelcabanillas.choirapi.service.ChatService;
import com.rafaelcabanillas.choirapi.service.CloudinaryService;
import com.rafaelcabanillas.choirapi.service.NotificationService;
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
    private final NotificationService notificationService;

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

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessageDTO sendMessage(@Payload NewMessageDTO chatMessage) {
        // 1. Save to DB
        ChatMessageDTO savedMsg = chatService.saveMessage(chatMessage);

        // 2. --- NOTIFICATION TRIGGER ---
        try {
            // Extract a simple preview of the text
            String preview = "Envió un archivo/imagen";
            // (You can implement logic here to extract text from JSON content if you want)

            notificationService.sendChatNotification(
                    savedMsg.getAuthor().getName(), // "Rafael sent..."
                    "Nuevo mensaje en el chat"      // Body
            );
        } catch (Exception e) {
            System.err.println("Error sending chat notification: " + e.getMessage());
        }
        // -----------------------------

        return savedMsg;
    }
}