package com.rafaelcabanillas.choirapi.controller;

import com.rafaelcabanillas.choirapi.dto.UpdateUserRequest;
import com.rafaelcabanillas.choirapi.dto.UserDTO;
import com.rafaelcabanillas.choirapi.service.NotificationService;
import com.rafaelcabanillas.choirapi.service.UserService;
import lombok.RequiredArgsConstructor;
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
    private final NotificationService notificationService; // Inject this

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateMyProfile(
            @RequestPart("data") UpdateUserRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(userService.updateMyProfile(request, file));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(
            @RequestPart("user") UpdateUserRequest request,
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws IOException {
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