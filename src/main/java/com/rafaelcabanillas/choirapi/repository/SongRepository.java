package com.rafaelcabanillas.choirapi.repository;

import com.rafaelcabanillas.choirapi.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findBySongTypeId(Long songTypeId);
}