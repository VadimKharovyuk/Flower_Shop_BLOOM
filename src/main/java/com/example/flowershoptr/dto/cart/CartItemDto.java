package com.example.flowershoptr.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CartItemDto {
    private Long id;
    private Long flowerId;
    private String flowerName;
    private String flowerImageUrl;
    private Integer quantity;

    private BigDecimal price;               // Обычная цена (без скидки)
    private BigDecimal discountPrice;       // Цена со скидкой (если есть)
    private boolean hasDiscount;
    private LocalDateTime discountExpiryDate;

    private BigDecimal itemTotal;           // Суммарная стоимость
}