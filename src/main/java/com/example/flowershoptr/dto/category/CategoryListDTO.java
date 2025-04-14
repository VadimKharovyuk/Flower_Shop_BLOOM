package com.example.flowershoptr.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryListDTO {
    private Long id;
    private String name;
    private String previewImageUrl;
    private boolean isFeatured;
    private boolean isActive;
    private int flowerCount;
    private String shortDescription;
    private Long cartAddCount;


}