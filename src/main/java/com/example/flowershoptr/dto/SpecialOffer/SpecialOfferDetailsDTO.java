package com.example.flowershoptr.dto.SpecialOffer;

import com.example.flowershoptr.dto.SpecialOffer.FlowerShortDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialOfferDetailsDTO {
    private Long id;
    private String name;
    private String description;
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
    private int savedAmount; // Вычисляемое поле - сумма экономии
    private List<FlowerShortDTO> applicableFlowers; // Краткая информация о цветах в акции
    private LocalDateTime createdAt;
}