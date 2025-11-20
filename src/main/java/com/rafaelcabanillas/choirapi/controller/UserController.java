package com.rafaelcabanillas.choirapi.controller;

import com.rafaelcabanillas.choirapi.dto.UpdateUserRequest;
import com.rafaelcabanillas.choirapi.dto.UserDTO;
import com.rafaelcabanillas.choirapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateMyProfile(@RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateMyProfile(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }
}