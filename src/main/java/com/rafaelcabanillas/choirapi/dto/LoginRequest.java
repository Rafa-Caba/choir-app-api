package com.rafaelcabanillas.choirapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String username; // Can be username OR email

    @NotBlank
    private String password;
}