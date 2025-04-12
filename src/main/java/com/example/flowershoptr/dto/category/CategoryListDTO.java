package com.example.flowershoptr.dto.category;

import lombok.Data;

@Data
public class CategoryListDTO {
    private Long id;
    private String name;
    private String previewImageUrl;
    private boolean isFeatured;
    private boolean isActive;
    private int flowerCount; // Количество цветов в категории
}