package com.rafaelcabanillas.choirapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "blog_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> content;

    // We store the author's name directly for display,
    // but you could link to User if you prefer strict relations.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id") // Database column will be author_id
    private User author;

    // Image
    private String imageUrl;
    private String imagePublicId;

    @Column(name = "is_public")
    @Builder.Default
    private boolean isPublic = false;

    // --- Relationships ---

    // 1. Likes: A list of Users who liked this post
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "blog_likes",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<User> likesUsers = new HashSet<>();

    // 2. Comments: A list of comment objects stored with the post
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "blog_comments", joinColumns = @JoinColumn(name = "post_id"))
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    // --- Timestamps ---
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}