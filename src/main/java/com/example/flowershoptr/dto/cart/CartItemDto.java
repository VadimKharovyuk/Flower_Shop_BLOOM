package com.example.flowershoptr.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

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
    private BigDecimal price;
    private BigDecimal itemTotal;
}