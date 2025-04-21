package com.example.flowershoptr.dto.ProductReview;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductReviewSummaryDTO {
    private Long flowerId;
    private Double averageRating;
    private Integer reviewCount;
    private Integer rating5Count;
    private Integer rating4Count;
    private Integer rating3Count;
    private Integer rating2Count;
    private Integer rating1Count;
}
