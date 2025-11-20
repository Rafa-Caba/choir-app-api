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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;

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
    public UserDTO updateMyProfile(UpdateUserRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return updateUserFields(user, request, false); // false = cannot change role
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
}