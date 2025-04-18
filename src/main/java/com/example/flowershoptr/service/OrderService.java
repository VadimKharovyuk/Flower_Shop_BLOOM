package com.example.flowershoptr.service;

import com.example.flowershoptr.dto.Order.CreateOrderDTO;
import com.example.flowershoptr.dto.Order.OrderDetailsDTO;
import com.example.flowershoptr.dto.Order.OrderListDTO;
import com.example.flowershoptr.enums.OrderStatus;
import com.example.flowershoptr.enums.PaymentStatus;
import com.example.flowershoptr.model.Order;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(CreateOrderDTO createOrderDTO, HttpSession session);

  Order getOrderById(Long id);

    List<Order> getOrdersByClientPhone(String phone);

    List<Order> getOrdersByOrderStatus(OrderStatus status);

    List<Order> getOrdersByPaymentStatus(PaymentStatus status);

    List<Order> getOrdersByDateRange(LocalDateTime start, LocalDateTime end);

    Order updateOrderStatus(Long orderId, OrderStatus status);

    Order updatePaymentStatus(Long orderId, PaymentStatus status);

    void cancelOrder(Long orderId);

    // Методы с использованием DTO для контроллеров
    OrderDetailsDTO getOrderDetails(Long orderId);

    List<OrderListDTO> getAllOrders();

    List<OrderListDTO> getOrdersByStatus(OrderStatus status);

    List<OrderDetailsDTO> getOrdersByPhone(String phone);

    Optional<OrderDetailsDTO> getOrderByNumber(String orderNumber);


    void updateOrderEmail(Long orderId, String email);

}