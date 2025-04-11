package com.example.flowershoptr.model;

import com.example.flowershoptr.enums.CollectionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "collections")
public class Collection {


    //Для секции "Свадебные букеты"

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "preview_image_url", length = 500)
    private String previewImageUrl;

    @Column
    private Long photoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "collection_type")
    private CollectionType type;

    @Column(name = "is_active")
    private boolean isActive = true;

    @ManyToMany
    @JoinTable(
            name = "collection_flowers",
            joinColumns = @JoinColumn(name = "collection_id"),
            inverseJoinColumns = @JoinColumn(name = "flower_id")
    )
    private Set<Flower> flowers = new HashSet<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}