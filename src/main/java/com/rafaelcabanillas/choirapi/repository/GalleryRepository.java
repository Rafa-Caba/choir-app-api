package com.rafaelcabanillas.choirapi.repository;

import com.rafaelcabanillas.choirapi.model.ImageGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GalleryRepository extends JpaRepository<ImageGallery, Long> {
    // Fetch images sorted by newest first
    List<ImageGallery> findAllByOrderByCreatedAtDesc();

    @Modifying
    @Query("UPDATE ImageGallery i SET i.imageStart = false")
    void clearImageStart();

    @Modifying
    @Query("UPDATE ImageGallery i SET i.imageTopBar = false")
    void clearImageTopBar();

    @Modifying
    @Query("UPDATE ImageGallery i SET i.imageUs = false")
    void clearImageUs();

    @Modifying
    @Query("UPDATE ImageGallery i SET i.imageLogo = false")
    void clearImageLogo();
}