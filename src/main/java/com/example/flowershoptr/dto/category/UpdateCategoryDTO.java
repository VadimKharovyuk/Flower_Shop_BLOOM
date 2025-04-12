package com.example.flowershoptr.dto.category;

import lombok.Data;

@Data
public class UpdateCategoryDTO {
    private Long id;
    private String name;
    private String description;
    private String previewImageUrl;
    private Long photoId;
    private boolean isFeatured;
    private boolean isActive;
}