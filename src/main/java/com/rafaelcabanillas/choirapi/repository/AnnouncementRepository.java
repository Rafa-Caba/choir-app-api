package com.rafaelcabanillas.choirapi.repository;

import com.rafaelcabanillas.choirapi.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    // For Admin: Latest first
    List<Announcement> findAllByOrderByCreatedAtDesc();

    // For App Users: Only published ones
    List<Announcement> findByIsPublicTrueOrderByCreatedAtDesc();
}