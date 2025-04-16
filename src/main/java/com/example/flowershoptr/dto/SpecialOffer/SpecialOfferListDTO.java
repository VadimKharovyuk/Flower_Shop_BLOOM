package com.example.flowershoptr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialOfferListDTO {
    private Long id;
    private String name;
    private String badgeText;
    private String imageUrl;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private String specialPriceText;
    private LocalDateTime endDate;
    private long remainingDays; // Вычисляемое поле - оставшиеся дни до окончания акции
    private String timerDisplayType;
    private boolean isFeatured;
    private String buttonText;
    private String buttonUrl;
    private boolean highlightButton;
    private int discountPercentage; // Вычисляемое поле - процент скидки
    private int flowerCount; // Количество цветов в этой акции
}