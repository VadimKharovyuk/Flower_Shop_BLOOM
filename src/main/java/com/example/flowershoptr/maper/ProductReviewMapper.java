package com.example.flowershoptr.maper;

import com.example.flowershoptr.dto.ProductReview.ProductReviewDTO;
import com.example.flowershoptr.dto.ProductReview.ProductReviewSummaryDTO;
import com.example.flowershoptr.model.ProductReview;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductReviewMapper {

    public ProductReviewDTO toDTO(ProductReview review) {
        // Проверка входных данных
        if (review == null) {
            throw new IllegalArgumentException("CreateProductReviewDTO не может быть null");
        }

        ProductReviewDTO dto = new ProductReviewDTO();
        dto.setId(review.getId());
        dto.setFlowerId(review.getFlower().getId());
        dto.setFlowerName(review.getFlower().getName());
        dto.setUserId(review.getUser().getId());
        dto.setUserName(review.getUser().getName());
        dto.setUserPictureUrl(review.getUser().getPictureUrl());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setVerified(review.isVerified());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }



    public ProductReviewSummaryDTO toSummaryDTO(Long flowerId, List<ProductReview> reviews) {
        ProductReviewSummaryDTO summary = new ProductReviewSummaryDTO();
        summary.setFlowerId(flowerId);

        if (reviews.isEmpty()) {
            summary.setAverageRating(0.0);
            summary.setReviewCount(0);
            summary.setRating5Count(0);
            summary.setRating4Count(0);
            summary.setRating3Count(0);
            summary.setRating2Count(0);
            summary.setRating1Count(0);
            return summary;
        }

        // Расчет среднего рейтинга
        double avgRating = reviews.stream()
                .mapToInt(ProductReview::getRating)
                .average()
                .orElse(0.0);
        summary.setAverageRating(Math.round(avgRating * 10) / 10.0); // округление до 1 знака
        summary.setReviewCount(reviews.size());

        // Подсчет количества отзывов по каждому рейтингу
        summary.setRating5Count((int) reviews.stream().filter(r -> r.getRating() == 5).count());
        summary.setRating4Count((int) reviews.stream().filter(r -> r.getRating() == 4).count());
        summary.setRating3Count((int) reviews.stream().filter(r -> r.getRating() == 3).count());
        summary.setRating2Count((int) reviews.stream().filter(r -> r.getRating() == 2).count());
        summary.setRating1Count((int) reviews.stream().filter(r -> r.getRating() == 1).count());

        return summary;
    }
}