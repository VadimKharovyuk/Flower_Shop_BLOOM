package com.example.flowershoptr.dto.SpecialOffer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialOfferCreateDTO {
    private String name;
    private String description;
    private String badgeText;
    private String imageUrl;
    private String publicId;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private String specialPriceText;
    private LocalDateTime endDate;
    private String timerDisplayType;
    private boolean featured;    // Изменено с isFeatured на featured
    private String buttonText;
    private String buttonUrl;
    private boolean highlightButton;
    private List<Long> flowerIds; // ID цветов, к которым будет применяться акция
    private boolean active;      // Изменено с isActive на active
}