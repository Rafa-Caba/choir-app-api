package com.rafaelcabanillas.choirapi.repository;

import com.rafaelcabanillas.choirapi.model.ImageGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GalleryRepository extends JpaRepository<ImageGallery, Long> {
    // Fetch images sorted by newest first
    List<ImageGallery> findAllByOrderByCreatedAtDesc();
}