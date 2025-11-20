package com.rafaelcabanillas.choirapi.repository;

import com.rafaelcabanillas.choirapi.model.SongType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SongTypeRepository extends JpaRepository<SongType, Long> {
    // Fetch types ordered by their specific order (1, 2, 3...)
    List<SongType> findAllByOrderByOrderAsc();
}