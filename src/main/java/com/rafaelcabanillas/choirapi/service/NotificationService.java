package com.rafaelcabanillas.choirapi.service;

import com.rafaelcabanillas.choirapi.model.User;
import com.rafaelcabanillas.choirapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String EXPO_API_URL = "https://exp.host/--/api/v2/push/send";

    // --- 1. Save Token ---
    public void saveToken(String username, String token) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            user.setPushToken(token);
            userRepository.save(user);
            log.info("Push token saved for user: {}", username);
        }
    }

    // --- 2. Send to ALL (Announcements/Blogs) ---
    @Async
    public void broadcastNotification(String title, String body) {
        List<String> allTokens = userRepository.findAll().stream()
                .map(User::getPushToken)
                .filter(token -> token != null && !token.isEmpty())
                .collect(Collectors.toList());

        sendToExpo(allTokens, title, body, null);
    }

    // --- 3. Send to Group (Chat) ---
    @Async
    public void sendChatNotification(String senderName, String messagePreview) {
        // Send to everyone EXCEPT the sender
        List<String> tokens = userRepository.findAll().stream()
                .filter(u -> !u.getUsername().equals(senderName)) // Don't notify myself
                .map(User::getPushToken)
                .filter(token -> token != null && !token.isEmpty())
                .collect(Collectors.toList());

        sendToExpo(tokens, "Nuevo mensaje de " + senderName, messagePreview, Map.of("screen", "Chat"));
    }

    // --- Internal Helper ---
    private void sendToExpo(List<String> tokens, String title, String body, Map<String, String> data) {
        if (tokens.isEmpty()) return;

        // Expo accepts batches of up to 100. For simplicity, we send one big batch list here.
        // In a huge app, you'd partition this list.

        Map<String, Object> payload = new HashMap<>();
        payload.put("to", tokens);
        payload.put("title", title);
        payload.put("body", body);
        payload.put("sound", "default");
        if (data != null) payload.put("data", data);

        try {
            restTemplate.postForObject(EXPO_API_URL, payload, String.class);
            log.info("Notification sent to {} devices", tokens.size());
        } catch (Exception e) {
            log.error("Error sending push notification", e);
        }
    }
}