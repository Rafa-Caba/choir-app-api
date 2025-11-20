package com.rafaelcabanillas.choirapi.controller;

import com.rafaelcabanillas.choirapi.dto.SettingDTO;
import com.rafaelcabanillas.choirapi.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @GetMapping
    public ResponseEntity<SettingDTO> getSettings() {
        return ResponseEntity.ok(settingService.getSettings());
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<SettingDTO> updateSettings(@RequestBody SettingDTO dto) {
        return ResponseEntity.ok(settingService.updateSettings(dto));
    }
}