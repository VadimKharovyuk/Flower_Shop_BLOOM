package com.example.flowershoptr.maper;

import com.example.flowershoptr.dto.flower.*;
import com.example.flowershoptr.model.Category;
import com.example.flowershoptr.model.Flower;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Component
public class FlowerMapper {


    public FlowerListDTO toListDTO(Flower flower) {
        if (flower == null) {
            return null;
        }

        FlowerListDTO dto = new FlowerListDTO();
        dto.setId(flower.getId());
        dto.setName(flower.getName());
        dto.setShortDescription(flower.getShortDescription());
        dto.setPreviewImageUrl(flower.getPreviewImageUrl());
        dto.setPrice(flower.getPrice());
        dto.setHasDeliveryToday(flower.isHasDeliveryToday());
        dto.setCount(flower.getCount());
        dto.setAverageRating(flower.getAverageRating());
        dto.setActive(flower.isActive());

        // Получаем имя категории, если категория существует
        if (flower.getCategory() != null) {
            dto.setCategoryName(flower.getCategory().getName());
        }
        if (flower.getCreatedAt() != null) {
            LocalDateTime now = LocalDateTime.now();
            long daysBetween = ChronoUnit.DAYS.between(flower.getCreatedAt(), now);
            dto.setNew(daysBetween < 14);
        } else {
            dto.setNew(false);
        }

        return dto;
    }


    public FlowerDetailsDTO toDetailsDTO(Flower flower) {
        if (flower == null) {
            return null;
        }

        FlowerDetailsDTO dto = new FlowerDetailsDTO();
        dto.setId(flower.getId());
        dto.setName(flower.getName());
        dto.setFullDescription(flower.getFullDescription());
        dto.setShortDescription(flower.getShortDescription());
        dto.setPreviewImageUrl(flower.getPreviewImageUrl());
        dto.setPhotoId(flower.getPhotoId());
        dto.setCount(flower.getCount());
        dto.setPrice(flower.getPrice());
        dto.setActive(flower.isActive());
        dto.setHasDeliveryToday(flower.isHasDeliveryToday());
        dto.setWeight(flower.getWeight());
        dto.setReviewCount(flower.getReviewCount());
        dto.setAverageRating(flower.getAverageRating());
        dto.setCreatedAt(flower.getCreatedAt());

        // Преобразуем категорию, если она существует
        if (flower.getCategory() != null) {
            FlowerDetailsDTO.CategoryDTO categoryDTO = new FlowerDetailsDTO.CategoryDTO();
            categoryDTO.setId(flower.getCategory().getId());
            categoryDTO.setName(flower.getCategory().getName());
            dto.setCategory(categoryDTO);
        }

        return dto;
    }


    public Flower toEntityFromCreateDTO(CreateFlowerDTO dto) {
        if (dto == null) {
            return null;
        }

        Flower flower = new Flower();
        flower.setName(dto.getName());
        flower.setFullDescription(dto.getFullDescription());
        flower.setShortDescription(dto.getShortDescription());
        flower.setPreviewImageUrl(dto.getPreviewImageUrl());
        flower.setPhotoId(dto.getPhotoId());
        flower.setCount(dto.getCount());
        flower.setPrice(dto.getPrice());
        flower.setActive(dto.isActive());
        flower.setHasDeliveryToday(dto.isHasDeliveryToday());
        flower.setWeight(dto.getWeight());

        // Установка значений по умолчанию
        flower.setReviewCount(0);
        flower.setAverageRating(0.0);


        // Категория будет установлена в сервисе после поиска по ID

        return flower;
    }


    public void updateFlowerFromDTO(Flower flower, UpdateFlowerDTO dto) {
        if (flower == null || dto == null) {
            return;
        }

        flower.setName(dto.getName());
        flower.setFullDescription(dto.getFullDescription());
        flower.setShortDescription(dto.getShortDescription());
        flower.setPreviewImageUrl(dto.getPreviewImageUrl());
        flower.setPhotoId(dto.getPhotoId());
        flower.setCount(dto.getCount());
        flower.setPrice(dto.getPrice());
        flower.setActive(dto.isActive());
        flower.setHasDeliveryToday(dto.isHasDeliveryToday());
        flower.setWeight(dto.getWeight());

        // Категория будет обновлена в сервисе после поиска по ID
    }

    public FlowerSearchDTO toFlowerSearchDTO(Flower flower) {
        if (flower == null) {
            return null;
        }

        FlowerSearchDTO dto = new FlowerSearchDTO();
        dto.setId(flower.getId());
        dto.setName(flower.getName());
        dto.setPreviewImageUrl(flower.getPreviewImageUrl());
        dto.setPrice(flower.getPrice());
        return dto;
    }

    public PopularFlowerDto toPopularDto(Flower flower) {
        PopularFlowerDto dto = new PopularFlowerDto();
        dto.setId(flower.getId());
        dto.setName(flower.getName());
        dto.setPreviewImageUrl(flower.getPreviewImageUrl());
        dto.setPrice(flower.getPrice());

        // Получаем название категории, если она существует
        String categoryName = flower.getCategory() != null ? flower.getCategory().getName() : null;
        dto.setCategoryName(categoryName);

        // Обработка null-значений для popularityCount
        Integer popularityCount = flower.getPopularityCount();
        dto.setPopularityCount(popularityCount != null ? popularityCount : 0);

        // Обработка null-значений для favoritesCount
        Integer favoritesCount = flower.getFavoritesCount();
        dto.setFavoritesCount(favoritesCount != null ? favoritesCount : 0);

        return dto;
    }

}