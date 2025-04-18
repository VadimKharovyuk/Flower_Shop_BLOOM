package com.example.flowershoptr.repository;

import com.example.flowershoptr.enums.PaymentStatus;
import com.example.flowershoptr.model.Payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByOrderId(Long orderId);
    Payment findByTransactionId(String transactionId);
}