package com.example.flowershoptr.controller.admin;

import com.example.flowershoptr.enums.PaymentStatus;
import com.example.flowershoptr.model.Order;
import com.example.flowershoptr.model.Payment;
import com.example.flowershoptr.repository.OrderRepository;
import com.example.flowershoptr.repository.PaymentRepository;
import com.example.flowershoptr.service.PaymentService;
import com.example.flowershoptr.service.payment.MonobankPaymentService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/monobank")
public class MonobankWebhookController {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String body) {
        try {
            JSONObject json = new JSONObject(body);
            String invoiceId = json.getString("invoiceId");
            String status = json.getString("status");

            Payment payment = paymentRepository.findByTransactionId(invoiceId);
            if (payment != null) {
                if ("success".equalsIgnoreCase(status)) {
                    payment.setStatus(PaymentStatus.COMPLETED);

                    // Обновляем статус заказа
                    Order order = payment.getOrder();
                    if (order != null) {
                        order.setPaymentStatus(PaymentStatus.COMPLETED);
                        orderRepository.save(order);
                    }
                } else if ("failure".equalsIgnoreCase(status)) {
                    payment.setStatus(PaymentStatus.FAILED);

                    // Обновляем статус заказа
                    Order order = payment.getOrder();
                    if (order != null) {
                        order.setPaymentStatus(PaymentStatus.FAILED);
                        orderRepository.save(order);
                    }
                } else {
                    payment.setStatus(PaymentStatus.PENDING);
                }
                paymentRepository.save(payment);
            }

            return ResponseEntity.ok("Webhook processed");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing webhook");
        }
    }
}