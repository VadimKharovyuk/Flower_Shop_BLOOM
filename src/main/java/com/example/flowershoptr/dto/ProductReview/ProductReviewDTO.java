package com.example.flowershoptr.dto.ProductReview;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ProductReviewDTO {
    private Long id;
    private Long flowerId;
    private String flowerName;
    private Long userId;
    private String userName;
    private String userPictureUrl;
    private Integer rating;
    private String comment;
    private boolean verified;
    private LocalDateTime createdAt;
}
