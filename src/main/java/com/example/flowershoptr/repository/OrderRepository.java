package com.example.flowershoptr.repository;

import com.example.flowershoptr.enums.OrderStatus;
import com.example.flowershoptr.enums.PaymentStatus;
import com.example.flowershoptr.model.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {


    List<Order> findByClientPhone(String phone);

    List<Order> findByOrderStatus(OrderStatus status);

    List<Order> findByPaymentStatus(PaymentStatus status);

    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    Optional<Order> findByOrderNumber(String orderNumber);

}
