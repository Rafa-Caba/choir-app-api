package com.rafaelcabanillas.choirapi.repository;

import com.rafaelcabanillas.choirapi.model.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ThemeRepository extends JpaRepository<Theme, Long> {
    // Standard CRUD methods (findById, save, etc.) are included automatically.

    // Helper to find by name (e.g., "Noche"), useful for seeding or defaults
    Optional<Theme> findByName(String name);
}