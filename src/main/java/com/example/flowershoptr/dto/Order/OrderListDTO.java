package com.example.flowershoptr.dto.Order;

import com.example.flowershoptr.enums.OrderStatus;
import com.example.flowershoptr.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderListDTO {
    private Long id;
    private String clientName;
    private String clientPhone;
    private LocalDateTime deliveryDate;
    private BigDecimal total;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
    private int itemCount; // Количество позиций в заказе
}