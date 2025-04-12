package com.example.flowershoptr.dto.category;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoryDetailsDTO {
    private Long id;
    private String name;
    private String description;
    private String previewImageUrl;
    private Long photoId;
    private boolean isFeatured;
    private boolean isActive;
    private LocalDateTime createdAt;
    private int flowerCount; // Количество цветов в категории
}