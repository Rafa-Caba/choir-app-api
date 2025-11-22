package com.rafaelcabanillas.choirapi.service;

import com.rafaelcabanillas.choirapi.dto.ThemeDTO;
import com.rafaelcabanillas.choirapi.model.Theme;
import com.rafaelcabanillas.choirapi.model.User;
import com.rafaelcabanillas.choirapi.repository.ThemeRepository;
import com.rafaelcabanillas.choirapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ThemeService {

    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;

    public List<ThemeDTO> getAllThemes() {
        return themeRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateUserTheme(Long themeId) {
        // 1. Get Current User
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Get Selected Theme
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new RuntimeException("Theme not found"));

        // 3. Update & Save
        user.setTheme(theme);
        userRepository.save(user);
    }

    @Transactional
    public ThemeDTO updateThemeDefinition(Long id, ThemeDTO dto) {
        Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Theme not found"));

        theme.setName(dto.getName());
        theme.setDark(dto.isDark());
        theme.setPrimaryColor(dto.getPrimaryColor());
        theme.setAccentColor(dto.getAccentColor());
        theme.setBackgroundColor(dto.getBackgroundColor());
        theme.setTextColor(dto.getTextColor());
        theme.setCardColor(dto.getCardColor());
        theme.setButtonColor(dto.getButtonColor());
        theme.setNavColor(dto.getNavColor());

        return toDTO(themeRepository.save(theme));
    }

    private ThemeDTO toDTO(Theme t) {
        return ThemeDTO.builder()
                .id(t.getId())
                .name(t.getName())
                .isDark(t.isDark())
                .primaryColor(t.getPrimaryColor())
                .accentColor(t.getAccentColor())
                .backgroundColor(t.getBackgroundColor())
                .textColor(t.getTextColor())
                .cardColor(t.getCardColor())
                .buttonColor(t.getButtonColor())
                .navColor(t.getNavColor())
                .build();
    }
}