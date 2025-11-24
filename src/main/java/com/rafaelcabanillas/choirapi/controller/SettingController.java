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

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<SettingDTO> updateSettings(
            @RequestPart("data") String dataJson, // <--- Fix
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        SettingDTO dto = objectMapper.readValue(dataJson, SettingDTO.class);
        // We ignore 'file' for now as service.updateSettings only takes DTO,
        // but accepting it prevents 415 errors if the frontend sends it.
        return ResponseEntity.ok(settingService.updateSettings(dto));
    }
}