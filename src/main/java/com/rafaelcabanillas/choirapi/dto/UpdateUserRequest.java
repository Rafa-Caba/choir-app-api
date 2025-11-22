package com.rafaelcabanillas.choirapi.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String username;
    private String email;
    private String password;
    private String instrument; // "Voz", "Guitarra", etc.
    private Boolean voice;     // true/false
    private String bio;
    private Long themeId;      // To change the app color scheme

    // We include 'role' here, but our Logic will ignore it
    // unless the requester is an ADMIN.
    private String role;
}