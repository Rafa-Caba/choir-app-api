package com.rafaelcabanillas.choirapi.service;

import com.rafaelcabanillas.choirapi.dto.GalleryDTO;
import com.rafaelcabanillas.choirapi.model.ImageGallery;
import com.rafaelcabanillas.choirapi.model.MediaType;
import com.rafaelcabanillas.choirapi.repository.GalleryRepository;
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
public class GalleryService {

    private final GalleryRepository galleryRepository;
    private final CloudinaryService cloudinaryService;

    public List<GalleryDTO> getAllImages() {
        return galleryRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public GalleryDTO uploadImage(String title, String description, MultipartFile file,
                                  boolean start, boolean topBar, boolean us, boolean logo, boolean gallery) throws IOException {

        // 1. Determine Type
        MediaType type = MediaType.IMAGE;
        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("video")) {
            type = MediaType.VIDEO;
        }

        // 2. Upload to Cloudinary (Ensure CloudinaryService uses "resource_type", "auto")
        // Use a specific folder for videos if you want, or keep it shared
        String folder = type == MediaType.VIDEO ? "choir/gallery_videos" : "choir/gallery";
        Map result = cloudinaryService.uploadFile(file, folder);

        String url = (String) result.get("secure_url");
        String publicId = (String) result.get("public_id");

        // 3. Save to DB
        ImageGallery image = ImageGallery.builder()
                .title(title)
                .description(description)
                .imageUrl(url)
                .imagePublicId(publicId)
                .mediaType(type) // Save type
                .imageStart(start)
                .imageTopBar(topBar)
                .imageUs(us)
                .imageLogo(logo)
                .imageGallery(gallery)
                .build();

        return toDTO(galleryRepository.save(image));
    }

    @Transactional
    public GalleryDTO updateImageFlags(Long id, Map<String, Boolean> flags) {
        ImageGallery image = galleryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        // Handle "Single Instance" flags (Mutual Exclusivity)
        if (Boolean.TRUE.equals(flags.get("imageStart"))) {
            galleryRepository.clearImageStart();
            image.setImageStart(true);
        }
        if (Boolean.TRUE.equals(flags.get("imageTopBar"))) {
            galleryRepository.clearImageTopBar();
            image.setImageTopBar(true);
        }
        if (Boolean.TRUE.equals(flags.get("imageUs"))) {
            galleryRepository.clearImageUs();
            image.setImageUs(true);
        }
        if (Boolean.TRUE.equals(flags.get("imageLogo"))) {
            galleryRepository.clearImageLogo();
            image.setImageLogo(true);
        }

        // Handle "Multiple Instance" flags (Toggle)
        if (flags.containsKey("imageGallery")) {
            image.setImageGallery(flags.get("imageGallery"));
        }

        // Handle Turning OFF single flags (if user unchecks the box)
        // Note: Usually we enforce one MUST be selected, but if you allow
        // having NO logo, you need this logic:
        if (Boolean.FALSE.equals(flags.get("imageStart"))) image.setImageStart(false);
        if (Boolean.FALSE.equals(flags.get("imageTopBar"))) image.setImageTopBar(false);
        if (Boolean.FALSE.equals(flags.get("imageUs"))) image.setImageUs(false);
        if (Boolean.FALSE.equals(flags.get("imageLogo"))) image.setImageLogo(false);

        return toDTO(galleryRepository.save(image));
    }

    @Transactional
    public void deleteImage(Long id) throws IOException {
        ImageGallery image = galleryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        if (image.getImagePublicId() != null) {
            cloudinaryService.deleteFile(image.getImagePublicId());
        }
        galleryRepository.delete(image);
    }

    private GalleryDTO toDTO(ImageGallery img) {
        return GalleryDTO.builder()
                .id(img.getId())
                .title(img.getTitle())
                .description(img.getDescription())
                .imageUrl(img.getImageUrl())
                .mediaType(img.getMediaType()) // Map type
                .imageStart(img.isImageStart())
                .imageTopBar(img.isImageTopBar())
                .imageUs(img.isImageUs())
                .imageLogo(img.isImageLogo())
                .imageGallery(img.isImageGallery())
                .createdAt(img.getCreatedAt())
                .build();
    }
}