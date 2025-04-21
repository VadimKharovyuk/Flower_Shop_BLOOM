package com.example.flowershoptr.service;

import com.example.flowershoptr.dto.ProductReview.CreateProductReviewDTO;
import com.example.flowershoptr.dto.ProductReview.ProductReviewDTO;
import com.example.flowershoptr.dto.ProductReview.ProductReviewSummaryDTO;
import com.example.flowershoptr.model.User;
import org.springframework.security.core.Authentication;

import java.util.List;


public interface ProductReviewService {
    // Создание отзыва (возвращает полные данные созданного отзыва)
    ProductReviewDTO createProductReview(CreateProductReviewDTO createDTO, String email );

    // Удаление отзыва с проверкой прав доступа
    void deleteProductReview(Long reviewId, User currentUser);

    // Получение отзыва по ID
    ProductReviewDTO getById(Long id);

    // Получение всех отзывов для конкретного продукта
    List<ProductReviewDTO> getReviewsByFlowerId(Long flowerId);

    // Получение статистики отзывов продукта
    ProductReviewSummaryDTO getReviewSummaryByFlowerId(Long flowerId);

    // Получение отзывов пользователя (для личного кабинета)
    List<ProductReviewDTO> getUserReviews(Long userId);

    // Проверка, может ли пользователь оставить отзыв на продукт
    boolean canUserReviewProduct(Long userId, Long flowerId);

    List<ProductReviewDTO> getCurrentUserReviews(Authentication authentication);

}
