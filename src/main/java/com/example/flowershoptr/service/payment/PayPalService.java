package com.example.flowershoptr.service.payment;

import com.example.flowershoptr.enums.OrderStatus;
import com.example.flowershoptr.enums.PaymentStatus;
import com.example.flowershoptr.model.Order;
import com.example.flowershoptr.model.Payment;
import com.example.flowershoptr.repository.OrderRepository;
import com.example.flowershoptr.repository.PaymentRepository;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
public class PayPalService {
    private final PayPalHttpClient payPalClient;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public PayPalService(PayPalHttpClient payPalClient,
                         PaymentRepository paymentRepository,
                         OrderRepository orderRepository) {
        this.payPalClient = payPalClient;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public Payment createPayment(Order order) throws IOException {
        OrdersCreateRequest request = new OrdersCreateRequest();
        request.prefer("return=representation");
        request.requestBody(buildRequestBody(order));

        HttpResponse<com.paypal.orders.Order> response = payPalClient.execute(request);
        com.paypal.orders.Order payPalOrder = response.result();

        // Create new payment
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(com.example.flowershoptr.enums.PaymentMethod.PAYPAL);
        payment.setAmount(order.getTotal());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId(payPalOrder.id());

        for (LinkDescription link : payPalOrder.links()) {
            if ("approve".equals(link.rel())) {
                payment.setPaymentUrl(link.href());
                break;
            }
        }

        return paymentRepository.save(payment);
    }

    public Payment capturePayment(String paymentId) throws IOException {
        Payment payment = paymentRepository.findByTransactionId(paymentId);

        if (payment == null) {
            throw new EntityNotFoundException("Payment not found for transaction ID: " + paymentId);
        }

        OrdersCaptureRequest request = new OrdersCaptureRequest(paymentId);

        try {
            HttpResponse<com.paypal.orders.Order> response = payPalClient.execute(request);
            com.paypal.orders.Order capturedOrder = response.result();

            switch(capturedOrder.status()) {
                case "COMPLETED":
                    payment.setStatus(PaymentStatus.COMPLETED);
                    payment.setPaidAt(LocalDateTime.now());

                    // Обновляем статус заказа
                    Order order = payment.getOrder();
                    order.setOrderStatus(OrderStatus.PAID);
                    orderRepository.save(order);

                    break;
                case "APPROVED":
                    payment.setStatus(PaymentStatus.PROCESSING);
                    break;
                case "VOIDED":
                    payment.setStatus(PaymentStatus.CANCELLED);
                    break;
                default:
                    payment.setStatus(PaymentStatus.FAILED);
                    break;
            }

            return paymentRepository.save(payment);
        } catch (Exception e) {
            // Обработка ошибок PayPal API
            payment.setStatus(PaymentStatus.FAILED);
            payment.setTransactionId(payment.getTransactionId() + " (Error: " + e.getMessage() + ")");
            return paymentRepository.save(payment);
        }
    }

    private OrderRequest buildRequestBody(Order order) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        ApplicationContext applicationContext = new ApplicationContext()
                .brandName("Flower Shop TR")
                .landingPage("BILLING")
                .cancelUrl(baseUrl + "/payment/cancel")
                .returnUrl(baseUrl + "/payment/success");
        orderRequest.applicationContext(applicationContext);

        List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
        PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest()
                .referenceId(order.getId().toString())
                .description("Order #" + order.getId())
                .amountWithBreakdown(new AmountWithBreakdown()
                        .currencyCode("USD")
                        .value(order.getTotal().toString()));

        purchaseUnits.add(purchaseUnit);
        orderRequest.purchaseUnits(purchaseUnits);

        return orderRequest;
    }
}