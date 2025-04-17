package com.example.flowershoptr.dto.ShopReview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopReviewListDTO {

    private Long id;
    private String userName;
    private String city;
    private String shoutDescription;
    private Integer rating;
    private String imageUrl;
    private LocalDateTime createdAt;
}