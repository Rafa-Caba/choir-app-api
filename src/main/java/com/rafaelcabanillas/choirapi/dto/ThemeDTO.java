package com.rafaelcabanillas.choirapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ThemeDTO {
    private Long id;
    private String name;
    private boolean isDark;

    // Core Colors
    private String primaryColor;
    private String accentColor;
    private String backgroundColor;
    private String textColor;
    private String cardColor;
    private String buttonColor;
    private String navColor;
}