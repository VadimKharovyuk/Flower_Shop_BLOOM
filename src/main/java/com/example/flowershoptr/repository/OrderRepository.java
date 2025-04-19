package com.example.flowershoptr.repository;

import com.example.flowershoptr.enums.OrderStatus;
import com.example.flowershoptr.enums.PaymentStatus;
import com.example.flowershoptr.model.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId OR (o.user IS NULL AND o.clientEmail = :email)")
    List<Order> findAllByUserIdOrEmail(Long userId, String email);

    @Query("SELECT o FROM Order o WHERE o.id = :orderId AND (o.user.id = :userId OR (o.user IS NULL AND o.clientEmail = :email))")
    Optional<Order> findByIdAndUserIdOrEmail(Long orderId, Long userId, String email);

}
