package com.rafaelcabanillas.choirapi.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class SettingDTO {
    private String appTitle;
    private SocialLinksDTO socialLinks;
    private Map<String, Object> aboutApp;
    private String appLogoUrl;

    @Data
    @Builder
    public static class SocialLinksDTO {
        private String facebook;
        private String instagram;
        private String youtube;
        private String whatsapp;
    }
}