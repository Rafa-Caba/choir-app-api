package com.rafaelcabanillas.choirapi.repository;

import com.rafaelcabanillas.choirapi.model.RefreshToken;
import com.rafaelcabanillas.choirapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByUser(User user);
}