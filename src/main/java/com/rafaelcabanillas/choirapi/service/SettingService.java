package com.rafaelcabanillas.choirapi.service;

import com.rafaelcabanillas.choirapi.dto.SettingDTO;
import com.rafaelcabanillas.choirapi.model.Setting;
import com.rafaelcabanillas.choirapi.repository.SettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final SettingRepository settingRepository;
    private static final Long SETTING_ID = 1L;

    public SettingDTO getSettings() {
        Setting setting = settingRepository.findById(SETTING_ID)
                .orElseThrow(() -> new RuntimeException("Settings not initialized in DB"));
        return toDTO(setting);
    }

    @Transactional
    public SettingDTO updateSettings(SettingDTO dto) {
        Setting setting = settingRepository.findById(SETTING_ID)
                .orElseThrow(() -> new RuntimeException("Settings not initialized"));

        // Update simple fields
        if (dto.getAppTitle() != null) setting.setAppTitle(dto.getAppTitle());
        if (dto.getAboutApp() != null) setting.setAboutApp(dto.getAboutApp());

        // Update nested social links
        if (dto.getSocialLinks() != null) {
            Setting.SocialLinks links = setting.getSocialLinks();
            if (links == null) links = new Setting.SocialLinks();

            links.setFacebook(dto.getSocialLinks().getFacebook());
            links.setInstagram(dto.getSocialLinks().getInstagram());
            links.setYoutube(dto.getSocialLinks().getYoutube());
            links.setWhatsapp(dto.getSocialLinks().getWhatsapp());

            setting.setSocialLinks(links);
        }

        return toDTO(settingRepository.save(setting));
    }

    private SettingDTO toDTO(Setting s) {
        Setting.SocialLinks links = s.getSocialLinks() != null ? s.getSocialLinks() : new Setting.SocialLinks();

        return SettingDTO.builder()
                .appTitle(s.getAppTitle())
                .aboutApp(s.getAboutApp())
                .socialLinks(SettingDTO.SocialLinksDTO.builder()
                        .facebook(links.getFacebook())
                        .instagram(links.getInstagram())
                        .youtube(links.getYoutube())
                        .whatsapp(links.getWhatsapp())
                        .build())
                .build();
    }
}