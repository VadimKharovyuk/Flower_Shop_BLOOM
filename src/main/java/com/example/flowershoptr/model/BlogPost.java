package com.example.flowershoptr.model;

import com.example.flowershoptr.enums.BlogPostType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "blog_posts")
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String fullContent;

    @Column(name = "preview_image_url", length = 500)
    private String previewImageUrl;

    @Column(name = "image_public_id")
    private String imagePublicId;

    @Enumerated(EnumType.STRING)
    private BlogPostType type;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}