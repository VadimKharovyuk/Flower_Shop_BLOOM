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
@Table(name = "flowers")
public class Flower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String name;

    private String fullDescription ;
    private String shortDescription;

    @Column(name = "preview_image_url", length = 500)
    private String previewImageUrl;

    @Column
    private Long photoId;

    private Integer count;
    private BigDecimal price;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "has_delivery_today")
    private boolean hasDeliveryToday;

    private double weight ;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
