package com.rafaelcabanillas.choirapi.service;

import com.rafaelcabanillas.choirapi.dto.SettingDTO;
import com.rafaelcabanillas.choirapi.model.Setting;
import com.rafaelcabanillas.choirapi.repository.SettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final SettingRepository settingRepository;
    private final CloudinaryService cloudinaryService;
    private static final Long SETTING_ID = 1L;

    public SettingDTO getSettings() {
        Setting setting = settingRepository.findById(SETTING_ID)
                .orElseThrow(() -> new RuntimeException("Settings not initialized in DB"));
        return toDTO(setting);
    }

    @Transactional
    public SettingDTO updateSettings(SettingDTO dto, MultipartFile file) throws IOException {
        Setting setting = settingRepository.findById(SETTING_ID)
                .orElseThrow(() -> new RuntimeException("Settings not initialized"));

        if (dto.getAppTitle() != null) setting.setAppTitle(dto.getAppTitle());
        if (dto.getAboutApp() != null) setting.setAboutApp(dto.getAboutApp());

        if (dto.getSocialLinks() != null) {
            Setting.SocialLinks links = setting.getSocialLinks();
            if (links == null) links = new Setting.SocialLinks();
            links.setFacebook(dto.getSocialLinks().getFacebook());
            links.setInstagram(dto.getSocialLinks().getInstagram());
            links.setYoutube(dto.getSocialLinks().getYoutube());
            links.setWhatsapp(dto.getSocialLinks().getWhatsapp());
            setting.setSocialLinks(links);
        }

        // Handle Logo Upload
        if (file != null && !file.isEmpty()) {
            if (setting.getAppLogoPublicId() != null) {
                cloudinaryService.deleteFile(setting.getAppLogoPublicId());
            }
            Map result = cloudinaryService.uploadFile(file, "choir/branding");
            setting.setAppLogoUrl((String) result.get("secure_url"));
            setting.setAppLogoPublicId((String) result.get("public_id"));
        }

        return toDTO(settingRepository.save(setting));
    }

    private SettingDTO toDTO(Setting s) {
        Setting.SocialLinks links = s.getSocialLinks() != null ? s.getSocialLinks() : new Setting.SocialLinks();

        return SettingDTO.builder()
                .appTitle(s.getAppTitle())
                .aboutApp(s.getAboutApp())
                .appLogoUrl(s.getAppLogoUrl())
                .socialLinks(SettingDTO.SocialLinksDTO.builder()
                        .facebook(links.getFacebook())
                        .instagram(links.getInstagram())
                        .youtube(links.getYoutube())
                        .whatsapp(links.getWhatsapp())
                        .build())
                .build();
    }
}