package com.rafaelcabanillas.choirapi.repository;

import com.rafaelcabanillas.choirapi.model.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<Setting, Long> {
    // No special methods needed, we always fetch by ID 1
}