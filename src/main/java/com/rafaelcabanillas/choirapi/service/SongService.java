package com.rafaelcabanillas.choirapi.service;

import com.rafaelcabanillas.choirapi.dto.SongDTO;
import com.rafaelcabanillas.choirapi.dto.SongTypeDTO;
import com.rafaelcabanillas.choirapi.model.Song;
import com.rafaelcabanillas.choirapi.model.SongType;
import com.rafaelcabanillas.choirapi.repository.SongRepository;
import com.rafaelcabanillas.choirapi.repository.SongTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final SongTypeRepository songTypeRepository;

    // --- Types ---

    public List<SongTypeDTO> getAllTypes() {
        return songTypeRepository.findAllByOrderByOrderAsc().stream()
                .map(t -> SongTypeDTO.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .order(t.getOrder())
                        .build())
                .collect(Collectors.toList());
    }

    // --- Songs ---

    public List<SongDTO> getAllSongs() {
        return songRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SongDTO createSong(SongDTO dto) {
        SongType type = songTypeRepository.findById(dto.getSongTypeId())
                .orElseThrow(() -> new RuntimeException("El tipo de Canto no fue encontrado"));

        Song song = Song.builder()
                .title(dto.getTitle())
                .composer(dto.getComposer())
                .content(dto.getContent()) // Directly saving the Map/JSON
                .songType(type)
                .build();

        return toDTO(songRepository.save(song));
    }

    @Transactional
    public SongDTO updateSong(Long id, SongDTO dto) {
        // 1. Find the existing song
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found with id: " + id));

        // 2. Update basic fields
        song.setTitle(dto.getTitle());
        song.setComposer(dto.getComposer());

        // 3. Update Rich Text Content (JSONB)
        if (dto.getContent() != null) {
            song.setContent(dto.getContent());
        }

        // 4. Update Relationship (if changed)
        if (dto.getSongTypeId() != null) {
            SongType type = songTypeRepository.findById(dto.getSongTypeId())
                    .orElseThrow(() -> new RuntimeException("Song Type not found"));
            song.setSongType(type);
        }

        // 5. Save and return
        return toDTO(songRepository.save(song));
    }

    private SongDTO toDTO(Song song) {
        return SongDTO.builder()
                .id(song.getId())
                .title(song.getTitle())
                .composer(song.getComposer())
                .content(song.getContent())
                .songTypeId(song.getSongType().getId())
                .songTypeName(song.getSongType().getName())
                .build();
    }
}