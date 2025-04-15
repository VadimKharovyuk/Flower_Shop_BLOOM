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
import java.util.ArrayList;
import java.util.List;

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
    @Column(columnDefinition = "TEXT")
    private String fullDescription ;

    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String previewImageUrl;

    @Column
    private Long photoId;


    ///в корзину __
    @Column(name = "popularity_count")
    private int popularityCount = 0;

    /// избраное
    @Column(name = "favorites_count")
    private Integer favoritesCount = 0;

    @Column(name = "cart_add_count")
    private Long cartAddCount = 0L;



    private Integer count;
    private BigDecimal price;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "has_delivery_today")
    private boolean hasDeliveryToday;

    private double weight ;

    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Column(name = "average_rating")
    private Double averageRating; // средний рейтнг



    // Добавляем связь со специальными предложениями
    @ManyToMany(mappedBy = "applicableFlowers")
    private List<SpecialOffer> specialOffers = new ArrayList<>();



    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
