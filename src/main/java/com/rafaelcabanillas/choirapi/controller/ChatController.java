package com.rafaelcabanillas.choirapi.controller;

import com.rafaelcabanillas.choirapi.dto.ChatMessageDTO;
import com.rafaelcabanillas.choirapi.dto.NewMessageDTO;
import com.rafaelcabanillas.choirapi.model.MessageType;
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

    // Special endpoint for uploading chat media (Images/Audio)
    @PostMapping("/api/chat/upload")
    public ResponseEntity<Map<String, String>> uploadMedia(@RequestParam("file") MultipartFile file) throws IOException {
        // Note: CloudinaryService must use "resource_type: auto" for this to work with Audio
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
        // --- DEBUG LOGS: Check Railway Logs for these lines ---
        System.out.println(">>> [WS] RECEIVED MESSAGE");
        System.out.println(">>> User: " + chatMessage.getUsername());
        System.out.println(">>> Type: " + chatMessage.getType());
        System.out.println(">>> AudioUrl: " + chatMessage.getAudioUrl());
        // -----------------------------------------------------

        try {
            // 1. Save to DB
            ChatMessageDTO savedMsg = chatService.saveMessage(chatMessage);
            System.out.println(">>> [WS] Message Saved Successfully. ID: " + savedMsg.getId());

            // 2. --- NOTIFICATION TRIGGER ---
            try {
                String preview = getNotificationPreview(savedMsg);

                notificationService.sendChatNotification(
                        savedMsg.getAuthor().getName(),
                        preview
                );
                System.out.println(">>> [WS] Notification sent: " + preview);
            } catch (Exception e) {
                // Don't fail the message if notification fails
                System.err.println(">>> [WS] Notification Error (Non-fatal): " + e.getMessage());
            }
            // -----------------------------

            return savedMsg;

        } catch (Exception e) {
            // 3. CRITICAL ERROR LOGGING
            System.err.println(">>> [WS] CRITICAL ERROR SAVING MESSAGE:");
            e.printStackTrace(); // This prints the full stack trace to logs
            throw e; // Throw it so the client knows it failed
        }
    }

    // Helper to format notification text based on message type
    private String getNotificationPreview(ChatMessageDTO msg) {
        if (msg.getType() == null) return "Nuevo mensaje";

        switch (msg.getType()) {
            case AUDIO:
                return "🎤 Nota de voz";
            case IMAGE:
                return "📷 Imagen";
            case FILE:
                return "📎 Archivo";
            case REACTION:
                return "Reaccionó a un mensaje";
            case TEXT:
            default:
                // Attempt to extract plain text from Tiptap JSON
                // JSON Structure: { type: "doc", content: [ { type: "paragraph", content: [ { text: "Hello" } ] } ] }
                try {
                    Map<String, Object> content = msg.getContent();
                    if (content != null && content.containsKey("content")) {
                        List<Map<String, Object>> paragraphs = (List) content.get("content");
                        if (!paragraphs.isEmpty() && paragraphs.get(0).containsKey("content")) {
                            List<Map<String, Object>> texts = (List) paragraphs.get(0).get("content");
                            if (!texts.isEmpty()) {
                                String text = (String) texts.get(0).get("text");
                                // Truncate if too long
                                return text.length() > 50 ? text.substring(0, 47) + "..." : text;
                            }
                        }
                    }
                    return "Nuevo mensaje de texto";
                } catch (Exception e) {
                    return "Nuevo mensaje";
                }
        }
    }
}