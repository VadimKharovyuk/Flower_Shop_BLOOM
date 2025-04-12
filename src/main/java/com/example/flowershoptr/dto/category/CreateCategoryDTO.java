package com.example.flowershoptr.dto.category;



import lombok.Data;

@Data
public class CreateCategoryDTO {
    private String name;
    private String description;
    private String previewImageUrl;
    private Long photoId;
    private boolean isFeatured;
    private boolean isActive = true;
}