package com.rafaelcabanillas.choirapi.service;

import com.rafaelcabanillas.choirapi.dto.GalleryDTO;
import com.rafaelcabanillas.choirapi.model.ImageGallery;
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

        // 1. Upload to Cloudinary
        Map result = cloudinaryService.uploadFile(file, "choir/gallery");
        String url = (String) result.get("secure_url");
        String publicId = (String) result.get("public_id");

        // 2. Save to DB
        ImageGallery image = ImageGallery.builder()
                .title(title)
                .description(description)
                .imageUrl(url)
                .imagePublicId(publicId)
                .imageStart(start)
                .imageTopBar(topBar)
                .imageUs(us)
                .imageLogo(logo)
                .imageGallery(gallery)
                .build();

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
                .imageStart(img.isImageStart())
                .imageTopBar(img.isImageTopBar())
                .imageUs(img.isImageUs())
                .imageLogo(img.isImageLogo())
                .imageGallery(img.isImageGallery())
                .createdAt(img.getCreatedAt())
                .build();
    }
}