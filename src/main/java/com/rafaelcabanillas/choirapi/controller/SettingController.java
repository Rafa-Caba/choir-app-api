package com.rafaelcabanillas.choirapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafaelcabanillas.choirapi.dto.SettingDTO;
import com.rafaelcabanillas.choirapi.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<SettingDTO> getSettings() {
        return ResponseEntity.ok(settingService.getSettings());
    }

    // FIX: Multipart support for Logo upload
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<SettingDTO> updateSettings(
            @RequestPart("data") String settingsJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        SettingDTO dto = objectMapper.readValue(settingsJson, SettingDTO.class);
        return ResponseEntity.ok(settingService.updateSettings(dto, file));
    }
}