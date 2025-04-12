package com.example.flowershoptr.maper;

import com.example.flowershoptr.dto.category.*;
import com.example.flowershoptr.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {


    public CategoryListDTO toListDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategoryListDTO dto = new CategoryListDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setPreviewImageUrl(category.getPreviewImageUrl());
        dto.setFeatured(category.isFeatured());
        dto.setActive(category.isActive());

        // Подсчитываем количество цветов в категории
        if (category.getFlowers() != null) {
            dto.setFlowerCount(category.getFlowers().size());
        } else {
            dto.setFlowerCount(0);
        }

        return dto;
    }


    public CategoryDetailsDTO toDetailsDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDetailsDTO dto = new CategoryDetailsDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setPreviewImageUrl(category.getPreviewImageUrl());
        dto.setPhotoId(category.getPhotoId());
        dto.setFeatured(category.isFeatured());
        dto.setActive(category.isActive());
        dto.setCreatedAt(category.getCreatedAt());

        // Подсчитываем количество цветов в категории
        if (category.getFlowers() != null) {
            dto.setFlowerCount(category.getFlowers().size());
        } else {
            dto.setFlowerCount(0);
        }

        return dto;
    }


    public CategorySimpleDTO toSimpleDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategorySimpleDTO dto = new CategorySimpleDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());

        return dto;
    }


    public Category toEntityFromCreateDTO(CreateCategoryDTO dto) {
        if (dto == null) {
            return null;
        }

        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setPreviewImageUrl(dto.getPreviewImageUrl());
        category.setPhotoId(dto.getPhotoId());
        category.setFeatured(dto.isFeatured());
        category.setActive(dto.isActive());

        return category;
    }


    public void updateCategoryFromDTO(Category category, UpdateCategoryDTO dto) {
        if (category == null || dto == null) {
            return;
        }

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setPreviewImageUrl(dto.getPreviewImageUrl());
        category.setPhotoId(dto.getPhotoId());
        category.setFeatured(dto.isFeatured());
        category.setActive(dto.isActive());
    }
}