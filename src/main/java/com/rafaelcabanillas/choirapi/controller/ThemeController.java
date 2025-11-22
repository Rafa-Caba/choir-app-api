package com.rafaelcabanillas.choirapi.controller;

import com.rafaelcabanillas.choirapi.dto.ThemeDTO;
import com.rafaelcabanillas.choirapi.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ThemeController {

    private final ThemeService themeService;

    // Public: Get all available themes
    @GetMapping("/themes")
    public ResponseEntity<List<ThemeDTO>> getAllThemes() {
        return ResponseEntity.ok(themeService.getAllThemes());
    }

    // Authenticated: Update my own theme
    @PutMapping("/users/me/theme")
    public ResponseEntity<Void> updateMyTheme(@RequestBody Map<String, Long> payload) {
        Long themeId = payload.get("themeId");
        themeService.updateUserTheme(themeId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ThemeDTO> updateTheme(
            @PathVariable Long id,
            @RequestBody ThemeDTO dto
    ) {
        return ResponseEntity.ok(themeService.updateThemeDefinition(id, dto));
    }
}