package com.example.flowershoptr.maper;



import com.example.flowershoptr.dto.ShopReview.ShopReviewCreateDTO;
import com.example.flowershoptr.dto.ShopReview.ShopReviewListDTO;
import com.example.flowershoptr.model.ShopReview;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Ручной маппер для преобразования между ShopReview и DTO
 */
@Component
public class ShopReviewMapper {

    /**
     * Преобразует DTO создания в сущность ShopReview
     */
    public ShopReview toEntity(ShopReviewCreateDTO dto) {
        ShopReview shopReview = new ShopReview();
        shopReview.setUserName(dto.getUserName());
        shopReview.setCity(dto.getCity());
        shopReview.setShoutDescription(dto.getShoutDescription());
        shopReview.setRating(dto.getRating());
        shopReview.setPreviewImageUrl(dto.getPreviewImageUrl());
        shopReview.setImagePublicId(dto.getImagePublicId());
        return shopReview;
    }

    /**
     * Преобразует сущность ShopReview в DTO для списка
     */
    public ShopReviewListDTO toListDTO(ShopReview shopReview) {
        ShopReviewListDTO dto = new ShopReviewListDTO();
        dto.setId(shopReview.getId());
        dto.setUserName(shopReview.getUserName());
        dto.setCity(shopReview.getCity());

        dto.setShoutDescription(shopReview.getShoutDescription());
        dto.setRating(shopReview.getRating());
        dto.setImageUrl(shopReview.getPreviewImageUrl());
        dto.setCreatedAt(shopReview.getCreatedAt());
        return dto;
    }


    public ShopReviewCreateDTO toCreateDTO(ShopReview savedReview) {
        ShopReviewCreateDTO dto = new ShopReviewCreateDTO();
        dto.setUserName(savedReview.getUserName());
        dto.setCity(savedReview.getCity());
        dto.setShoutDescription(savedReview.getShoutDescription());
        dto.setRating(savedReview.getRating());
        dto.setPreviewImageUrl(savedReview.getPreviewImageUrl());
        dto.setImagePublicId(savedReview.getImagePublicId());
        return dto;
    }
}