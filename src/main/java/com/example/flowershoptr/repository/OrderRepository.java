package com.example.flowershoptr.repository;

import com.example.flowershoptr.dto.Order.OrderListDTO;
import com.example.flowershoptr.enums.OrderStatus;
import com.example.flowershoptr.enums.PaymentStatus;
import com.example.flowershoptr.model.Order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<Order> findByUserEmailOrUserId(String email, Long id);


    @Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.items i WHERE o.clientEmail = :email AND i.flower.id = :flowerId")
    boolean existsByEmailAndFlowerId(@Param("email") String email, @Param("flowerId") Long flowerId);

    @Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.items i WHERE o.user.id = :userId AND i.flower.id = :flowerId")
    boolean existsByUserIdAndFlowerId(@Param("userId") Long userId, @Param("flowerId") Long flowerId);

}
