package com.rafaelcabanillas.choirapi.service;

import com.rafaelcabanillas.choirapi.dto.UpdateUserRequest;
import com.rafaelcabanillas.choirapi.dto.UserDTO;
import com.rafaelcabanillas.choirapi.model.Role;
import com.rafaelcabanillas.choirapi.model.Theme;
import com.rafaelcabanillas.choirapi.model.User;
import com.rafaelcabanillas.choirapi.repository.ThemeRepository;
import org.springframework.transaction.annotation.Transactional;
import com.rafaelcabanillas.choirapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;

    // Get the user who is currently sending the request
    public UserDTO getCurrentUserProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return UserDTO.fromEntity(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO updateMyProfile(UpdateUserRequest request, MultipartFile file) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Handle Image Upload
        if (file != null && !file.isEmpty()) {
            // Optional: Delete old image if exists
            if (user.getImagePublicId() != null) {
                try {
                    cloudinaryService.deleteFile(user.getImagePublicId());
                } catch (Exception e) {
                    // Log error but continue
                }
            }

            Map uploadResult = cloudinaryService.uploadFile(file, "choir/users");
            user.setImageUrl((String) uploadResult.get("secure_url"));
            user.setImagePublicId((String) uploadResult.get("public_id"));
        }

        return updateUserFields(user, request, false);
    }

    @Transactional
    public UserDTO updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        return updateUserFields(user, request, true); // true = can change role
    }

    private UserDTO updateUserFields(User user, UpdateUserRequest req, boolean isAdmin) {
        if (req.getName() != null) user.setName(req.getName());
        if (req.getInstrument() != null) user.setInstrument(req.getInstrument());
        if (req.getVoice() != null) user.setVoice(req.getVoice());
        if (req.getBio() != null) user.setBio(req.getBio());

        // Handle Email Change (Check uniqueness)
        if (req.getEmail() != null && !req.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(req.getEmail())) {
                throw new RuntimeException("Email already in use");
            }
            user.setEmail(req.getEmail());
        }

        // Handle Theme Change
        if (req.getThemeId() != null) {
            Theme theme = themeRepository.findById(req.getThemeId())
                    .orElseThrow(() -> new RuntimeException("Theme not found"));
            user.setTheme(theme);
        }

        // Handle Role (Only if Admin)
        if (isAdmin && req.getRole() != null) {
            try {
                user.setRole(Role.valueOf(req.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid Role");
            }
        }

        return UserDTO.fromEntity(userRepository.save(user));
    }

    @Transactional
    public UserDTO createUser(UpdateUserRequest req, MultipartFile file) throws IOException {
        // 1. Validate Uniqueness
        if (userRepository.existsByEmail(req.getEmail())) throw new RuntimeException("Email already in use");
        if (userRepository.existsByUsername(req.getUsername())) throw new RuntimeException("Username already taken");

        // 2. Handle Image Upload
        String url = null;
        String publicId = null;
        if (file != null && !file.isEmpty()) {
            Map uploadResult = cloudinaryService.uploadFile(file, "choir/users");
            url = (String) uploadResult.get("secure_url");
            publicId = (String) uploadResult.get("public_id");
        }

        // 3. Build User
        User user = User.builder()
                .name(req.getName())
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword())) // Hash the password!
                .instrument(req.getInstrument() != null ? req.getInstrument() : "Voz")
                .bio(req.getBio())
                .imageUrl(url)
                .imagePublicId(publicId)
                .role(Role.USER) // Default, overridden below
                .build();

        // 4. Set Role (if provided)
        if (req.getRole() != null) {
            try {
                user.setRole(Role.valueOf(req.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // ignore invalid role
            }
        }

        return UserDTO.fromEntity(userRepository.save(user));
    }
}