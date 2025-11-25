package com.rafaelcabanillas.choirapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafaelcabanillas.choirapi.dto.UpdateUserRequest;
import com.rafaelcabanillas.choirapi.dto.UserDTO;
import com.rafaelcabanillas.choirapi.service.NotificationService;
import com.rafaelcabanillas.choirapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDTO> updateMyProfile(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        UpdateUserRequest request = objectMapper.readValue(dataJson, UpdateUserRequest.class);
        return ResponseEntity.ok(userService.updateMyProfile(request, file));
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @RequestPart("user") String dataJson,
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws IOException {
        UpdateUserRequest request = objectMapper.readValue(dataJson, UpdateUserRequest.class);
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(
            @RequestPart("user") String dataJson,
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws IOException {
        UpdateUserRequest request = objectMapper.readValue(dataJson, UpdateUserRequest.class);
        return ResponseEntity.ok(userService.createUser(request, file));
    }

    @PutMapping("/me/push-token")
    public ResponseEntity<Void> updatePushToken(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        notificationService.saveToken(username, token);
        return ResponseEntity.ok().build();
    }
}