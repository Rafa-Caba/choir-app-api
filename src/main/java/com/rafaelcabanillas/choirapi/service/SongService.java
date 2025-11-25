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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final SongTypeRepository songTypeRepository;
    private final CloudinaryService cloudinaryService;

    // --- TYPES ---

    public List<SongTypeDTO> getAllTypes() {
        return songTypeRepository.findAllByOrderByOrderAsc().stream()
                .map(t -> SongTypeDTO.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .order(t.getOrder())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public SongTypeDTO createType(SongTypeDTO dto) {
        SongType type = SongType.builder()
                .name(dto.getName())
                .order(dto.getOrder() != null ? dto.getOrder() : 99)
                .build();
        type = songTypeRepository.save(type);
        return mapTypeToDTO(type);
    }

    @Transactional
    public SongTypeDTO updateType(Long id, SongTypeDTO dto) {
        SongType type = songTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Type not found"));
        type.setName(dto.getName());
        if(dto.getOrder() != null) type.setOrder(dto.getOrder());
        return mapTypeToDTO(songTypeRepository.save(type));
    }

    @Transactional
    public void deleteType(Long id) {
        // Optional: Check if songs exist for this type before deleting?
        songTypeRepository.deleteById(id);
    }

    private SongTypeDTO mapTypeToDTO(SongType t) {
        return SongTypeDTO.builder().id(t.getId()).name(t.getName()).order(t.getOrder()).build();
    }

    // --- SONGS ---

    public List<SongDTO> getAllSongs() {
        return songRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SongDTO createSong(SongDTO dto, MultipartFile audioFile) throws IOException {
        SongType type = songTypeRepository.findById(dto.getSongTypeId())
                .orElseThrow(() -> new RuntimeException("El tipo de Canto no fue encontrado"));

        Song song = Song.builder()
                .title(dto.getTitle())
                .composer(dto.getComposer())
                .content(dto.getContent())
                .songType(type)
                .build();

        // Handle Audio Upload
        if (audioFile != null && !audioFile.isEmpty()) {
            // Upload as 'video' resource_type (Cloudinary treats audio as video)
            Map<String, Object> uploadResult = cloudinaryService.uploadFile(audioFile, "choir/songs_audio");
            song.setAudioUrl((String) uploadResult.get("secure_url"));
            song.setAudioPublicId((String) uploadResult.get("public_id"));
        }

        return toDTO(songRepository.save(song));
    }

    @Transactional
    public SongDTO updateSong(Long id, SongDTO dto, MultipartFile audioFile) throws IOException {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        song.setTitle(dto.getTitle());
        song.setComposer(dto.getComposer());

        if (dto.getContent() != null) {
            song.setContent(dto.getContent());
        }

        if (dto.getSongTypeId() != null) {
            SongType type = songTypeRepository.findById(dto.getSongTypeId())
                    .orElseThrow(() -> new RuntimeException("Song Type not found"));
            song.setSongType(type);
        }

        // Handle Audio Replacement
        if (audioFile != null && !audioFile.isEmpty()) {
            // Delete old audio if exists
            if (song.getAudioPublicId() != null) {
                cloudinaryService.deleteFile(song.getAudioPublicId());
            }
            Map<String, Object> uploadResult = cloudinaryService.uploadFile(audioFile, "choir/songs_audio");
            song.setAudioUrl((String) uploadResult.get("secure_url"));
            song.setAudioPublicId((String) uploadResult.get("public_id"));
        }

        return toDTO(songRepository.save(song));
    }

    @Transactional
    public void deleteSong(Long id) throws IOException {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        // Delete audio from Cloudinary
        if (song.getAudioPublicId() != null) {
            cloudinaryService.deleteFile(song.getAudioPublicId());
        }

        songRepository.delete(song);
    }

    private SongDTO toDTO(Song song) {
        return SongDTO.builder()
                .id(song.getId())
                .title(song.getTitle())
                .composer(song.getComposer())
                .content(song.getContent())
                .songTypeId(song.getSongType().getId())
                .songTypeName(song.getSongType().getName())
                .audioUrl(song.getAudioUrl()) // Include Audio in DTO
                .build();
    }
}