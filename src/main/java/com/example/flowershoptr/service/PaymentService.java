package com.example.flowershoptr.service;

import com.example.flowershoptr.model.Order;
import com.example.flowershoptr.model.Payment;

public interface PaymentService {
    Payment processPayment(Order order, String returnUrl, String cancelUrl);
    Payment completePayment(String paymentId, String payerId);
    Payment getPaymentById(Long id);

    String getSignature(String data);
}