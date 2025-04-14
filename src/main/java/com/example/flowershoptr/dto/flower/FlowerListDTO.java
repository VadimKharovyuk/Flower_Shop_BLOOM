package com.example.flowershoptr.dto.flower;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FlowerListDTO {
    private Long id;
    private String name;
    private String shortDescription;
    private String previewImageUrl;
    private BigDecimal price;
    private boolean hasDeliveryToday;
    private Integer count;
    private Double averageRating;
    private boolean isActive;
    private String categoryName;
    private boolean isNew;
}