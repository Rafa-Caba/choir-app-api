package com.rafaelcabanillas.choirapi.service;

import com.rafaelcabanillas.choirapi.dto.AuthResponse;
import com.rafaelcabanillas.choirapi.dto.LoginRequest;
import com.rafaelcabanillas.choirapi.dto.RegisterRequest;
import com.rafaelcabanillas.choirapi.model.RefreshToken;
import com.rafaelcabanillas.choirapi.model.Role;
import com.rafaelcabanillas.choirapi.model.User;
import com.rafaelcabanillas.choirapi.repository.UserRepository;
import com.rafaelcabanillas.choirapi.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1. Check if exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // 2. Create User
        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // Default role
                .instrument(request.getInstrument() != null ? request.getInstrument() : "Voz")
                .voice(request.getVoice() != null ? request.getVoice() : true)
                .build();

        userRepository.save(user);

        // 3. Generate Tokens immediately so they are logged in
        String jwt = jwtUtil.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // 1. Authenticate (works with username OR email via our Custom UserDetails)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Get User Details
        String username = request.getUsername();
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Generate Tokens
        String jwt = jwtUtil.generateToken(user);

        // Delete any old refresh tokens for a clean slate, then create new
        refreshTokenService.deleteByUserId(user.getId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse refreshToken(String requestRefreshToken) {
        // Rotate: Verify old -> Delete old -> Create new
        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(requestRefreshToken);
        User user = newRefreshToken.getUser();
        String newJwt = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .accessToken(newJwt)
                .refreshToken(newRefreshToken.getToken())
                .tokenType("Bearer")
                .role(user.getRole().name())
                .build();
    }
}