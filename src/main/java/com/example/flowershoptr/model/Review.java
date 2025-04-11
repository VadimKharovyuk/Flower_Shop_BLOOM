package com.example.flowershoptr.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "author_name", nullable = false)
    private String authorName;

    @Column(name = "author_location")
    private String authorLocation;

    @Column(name = "author_photo_url")
    private String authorPhotoUrl;

    @Column(name = "author_photo_id")
    private Long authorPhotoId;

    @Column(length = 1000, nullable = false)
    private String content;

    // Рейтинг от 1 до 5
    @Column(nullable = false)
    private Integer rating;

    // Связь с цветком, если отзыв относится к конкретному товару
    @ManyToOne
    @JoinColumn(name = "flower_id")
    private Flower flower;

    // Флаг для отображения на главной странице
    @Column(name = "is_featured")
    private boolean isFeatured = false;

    // Флаг модерации
    @Column(name = "is_approved")
    private boolean isApproved = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}