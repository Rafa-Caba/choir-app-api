package com.rafaelcabanillas.choirapi.repository;

import com.rafaelcabanillas.choirapi.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    // Admin sees everything
    List<BlogPost> findAllByOrderByCreatedAtDesc();

    // Public sees only published posts
    List<BlogPost> findByIsPublicTrueOrderByCreatedAtDesc();
}