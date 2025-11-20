package com.rafaelcabanillas.choirapi.service;

import com.rafaelcabanillas.choirapi.dto.AnnouncementDTO;
import com.rafaelcabanillas.choirapi.model.Announcement;
import com.rafaelcabanillas.choirapi.repository.AnnouncementRepository;
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
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final CloudinaryService cloudinaryService;

    public List<AnnouncementDTO> getAll(boolean adminMode) {
        List<Announcement> list = adminMode
                ? announcementRepository.findAllByOrderByCreatedAtDesc()
                : announcementRepository.findByIsPublicTrueOrderByCreatedAtDesc();

        return list.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public AnnouncementDTO create(String title, Map<String, Object> content, boolean isPublic, MultipartFile file) throws IOException {
        String url = null;
        String publicId = null;

        if (file != null && !file.isEmpty()) {
            Map uploadResult = cloudinaryService.uploadFile(file, "choir/announcements");
            url = (String) uploadResult.get("secure_url");
            publicId = (String) uploadResult.get("public_id");
        }

        Announcement entity = Announcement.builder()
                .title(title)
                .content(content)
                .isPublic(isPublic)
                .imageUrl(url)
                .imagePublicId(publicId)
                .build();

        return toDTO(announcementRepository.save(entity));
    }

    @Transactional
    public AnnouncementDTO update(Long id, String title, Map<String, Object> content, boolean isPublic, MultipartFile file) throws IOException {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setPublic(isPublic);

        // Only update image if a new file is provided
        if (file != null && !file.isEmpty()) {
            // Optional: Delete old image from Cloudinary first
            if (announcement.getImagePublicId() != null) {
                cloudinaryService.deleteFile(announcement.getImagePublicId());
            }
            Map uploadResult = cloudinaryService.uploadFile(file, "choir/announcements");
            announcement.setImageUrl((String) uploadResult.get("secure_url"));
            announcement.setImagePublicId((String) uploadResult.get("public_id"));
        }

        return toDTO(announcementRepository.save(announcement));
    }

    @Transactional
    public void delete(Long id) throws IOException {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        if (announcement.getImagePublicId() != null) {
            cloudinaryService.deleteFile(announcement.getImagePublicId());
        }
        announcementRepository.delete(announcement);
    }

    private AnnouncementDTO toDTO(Announcement a) {
        return AnnouncementDTO.builder()
                .id(a.getId())
                .title(a.getTitle())
                .content(a.getContent())
                .imageUrl(a.getImageUrl())
                .isPublic(a.isPublic())
                .createdAt(a.getCreatedAt())
                .build();
    }
}