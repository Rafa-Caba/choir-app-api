package com.rafaelcabanillas.choirapi.service;

import com.rafaelcabanillas.choirapi.dto.BlogPostDTO;
import com.rafaelcabanillas.choirapi.dto.CommentDTO;
import com.rafaelcabanillas.choirapi.model.BlogPost;
import com.rafaelcabanillas.choirapi.model.Comment;
import com.rafaelcabanillas.choirapi.model.User;
import com.rafaelcabanillas.choirapi.repository.BlogPostRepository;
import com.rafaelcabanillas.choirapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogPostRepository blogRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final NotificationService notificationService;

    // --- READ ---

    public List<BlogPostDTO> getAll(boolean isAdmin) {
        List<BlogPost> posts = isAdmin
                ? blogRepository.findAllByOrderByCreatedAtDesc()
                : blogRepository.findByIsPublicTrueOrderByCreatedAtDesc();

        return posts.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public BlogPostDTO getById(Long id) {
        BlogPost post = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return toDTO(post);
    }

    // --- WRITE (Admin) ---

    @Transactional
    public BlogPostDTO create(String title, Map<String, Object> content, boolean isPublic, MultipartFile file) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String url = null;
        String publicId = null;
        if (file != null && !file.isEmpty()) {
            Map uploadResult = cloudinaryService.uploadFile(file, "choir/blog");
            url = (String) uploadResult.get("secure_url");
            publicId = (String) uploadResult.get("public_id");
        }

        BlogPost post = BlogPost.builder()
                .title(title)
                .content(content)
                .author(user)
                .isPublic(isPublic)
                .imageUrl(url)
                .imagePublicId(publicId)
                .build();

        // 1. Save to DB
        BlogPost savedPost = blogRepository.save(post);

        // 2. --- NOTIFICATION TRIGGER ---
        if (isPublic) {
            try {
                notificationService.broadcastNotification("Nueva Publicación en el Blog 📝", title);
            } catch (Exception e) {
                System.err.println("Error sending notification: " + e.getMessage());
            }
        }
        // -----------------------------

        return toDTO(savedPost);
    }

    @Transactional
    public BlogPostDTO update(Long id, String title, Map<String, Object> content, boolean isPublic, MultipartFile file) throws IOException {
        BlogPost post = blogRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));

        post.setTitle(title);
        if (content != null) post.setContent(content);
        post.setPublic(isPublic);

        if (file != null && !file.isEmpty()) {
            if (post.getImagePublicId() != null) {
                cloudinaryService.deleteFile(post.getImagePublicId());
            }
            Map uploadResult = cloudinaryService.uploadFile(file, "choir/blog");
            post.setImageUrl((String) uploadResult.get("secure_url"));
            post.setImagePublicId((String) uploadResult.get("public_id"));
        }

        return toDTO(blogRepository.save(post));
    }

    @Transactional
    public void delete(Long id) throws IOException {
        BlogPost post = blogRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
        if (post.getImagePublicId() != null) {
            cloudinaryService.deleteFile(post.getImagePublicId());
        }
        blogRepository.delete(post);
    }

    // --- INTERACTIONS (User) ---

    @Transactional
    public BlogPostDTO toggleLike(Long postId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        BlogPost post = blogRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        if (post.getLikesUsers().contains(user)) {
            post.getLikesUsers().remove(user); // Unlike
        } else {
            post.getLikesUsers().add(user);    // Like
        }

        return toDTO(blogRepository.save(post));
    }

    @Transactional
    public BlogPostDTO addComment(Long postId, Map<String, Object> text) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        BlogPost post = blogRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = Comment.builder()
                .author(username)
                .text(text) // Rich text JSON
                .date(OffsetDateTime.now())
                .build();

        post.getComments().add(comment);
        return toDTO(blogRepository.save(post));
    }

    // --- HELPER ---

    private BlogPostDTO toDTO(BlogPost post) {
        BlogPostDTO.AuthorInfo authorInfo = null;
        if (post.getAuthor() != null) {
            authorInfo = BlogPostDTO.AuthorInfo.builder()
                    .id(post.getAuthor().getId())
                    .name(post.getAuthor().getName())
                    .username(post.getAuthor().getUsername())
                    .imageUrl(post.getAuthor().getImageUrl())
                    .build();
        }

        return BlogPostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(authorInfo)
                .imageUrl(post.getImageUrl())
                .imageUrl(post.getImageUrl())
                .isPublic(post.isPublic())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                // Map Relationships
                .likes(post.getLikesUsers().size())
                .likesUsers(post.getLikesUsers().stream().map(User::getUsername).collect(Collectors.toList()))
                .comments(post.getComments().stream().map(c -> CommentDTO.builder()
                        .author(c.getAuthor())
                        .text(c.getText())
                        .date(c.getDate())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}