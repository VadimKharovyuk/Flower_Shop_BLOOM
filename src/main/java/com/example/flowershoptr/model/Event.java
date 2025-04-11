package com.example.flowershoptr.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 500)
    private String description;

    @Column(name = "event_type") // MASTERCLASS, WORKSHOP, MEETING
    private String eventType;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    private BigDecimal price;

    @Column(name = "is_free")
    private boolean isFree = false;

    @Column(name = "preview_image_url", length = 500)
    private String previewImageUrl;

    @Column
    private Long photoId;

    @Column(name = "is_featured")
    private boolean isFeatured = false;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "available_seats")
    private Integer availableSeats;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}