package com.example.flowershoptr.dto.Order;

import com.example.flowershoptr.enums.OrderStatus;
import com.example.flowershoptr.enums.PaymentMethod;
import com.example.flowershoptr.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsDTO {
    private Long id;
    private String clientName;
    private String clientPhone;
    private String clientEmail;
    private String deliveryAddress;
    private LocalDateTime deliveryDate;
    private String notes;
    private BigDecimal total;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private OrderStatus orderStatus;
    private List<OrderItemDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String order_number ;
}