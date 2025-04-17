package com.example.flowershoptr.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "instagram_posts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstagramPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    private String postUrl;

    private String caption;

    @Column(nullable = false)
    private boolean active = true;

    private int displayOrder;

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