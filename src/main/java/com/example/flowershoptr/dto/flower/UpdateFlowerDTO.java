package com.example.flowershoptr.dto.flower;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateFlowerDTO {
    private Long id; // ID обязателен для обновления
    private Long categoryId;
    private String name;
    private String fullDescription;
    private String shortDescription;
    private String previewImageUrl;
    private Long photoId;
    private Integer count;
    private BigDecimal price;
    private boolean isActive;
    private boolean hasDeliveryToday;
    private double weight;
}