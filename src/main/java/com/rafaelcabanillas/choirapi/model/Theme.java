package com.rafaelcabanillas.choirapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "themes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Theme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // "Light", "Dark"

    @Builder.Default
    private boolean isDark = false;

    // Core Colors
    private String primaryColor;
    private String accentColor;
    private String backgroundColor;
    private String textColor;
    private String cardColor;
    private String buttonColor;
    private String navColor;
}