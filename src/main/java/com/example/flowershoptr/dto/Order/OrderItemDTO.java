package com.example.flowershoptr.dto.Order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long id;
    private Long flowerId;
    private String flowerName;
    private String flowerImage;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal itemTotal;
}