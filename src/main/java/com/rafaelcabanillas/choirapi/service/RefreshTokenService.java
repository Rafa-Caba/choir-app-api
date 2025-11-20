package com.rafaelcabanillas.choirapi.service;

import com.rafaelcabanillas.choirapi.model.RefreshToken;
import com.rafaelcabanillas.choirapi.model.User;
import com.rafaelcabanillas.choirapi.repository.RefreshTokenRepository;
import com.rafaelcabanillas.choirapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.refresh-expiration:604800000}") // 7 days default
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId).get();

        // Create new token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }

    // Token Rotation (Security Best Practice)
    @Transactional
    public RefreshToken rotateRefreshToken(String oldToken) {
        // 1. Find and verify old token
        RefreshToken old = findByToken(oldToken)
                .map(this::verifyExpiration)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        // 2. Get user
        User user = old.getUser();

        // 3. Delete old token
        refreshTokenRepository.delete(old);

        // 4. Create new one
        return createRefreshToken(user.getId());
    }
}