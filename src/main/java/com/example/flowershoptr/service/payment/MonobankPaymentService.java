package com.example.flowershoptr.service.payment;

import com.example.flowershoptr.enums.PaymentMethod;
import com.example.flowershoptr.enums.PaymentStatus;
import com.example.flowershoptr.model.Order;
import com.example.flowershoptr.model.Payment;
import com.example.flowershoptr.repository.PaymentRepository;
import com.example.flowershoptr.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class MonobankPaymentService implements PaymentService {

    @Value("${monobank.api.token}")
    private String apiToken;

    private static final String MONOBANK_URL = "https://api.monobank.ua/api/merchant/invoice/create";

    private final PaymentRepository paymentRepository;


    @Override
    public Payment processPayment(Order order, String returnUrl, String cancelUrl) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            String body = String.format("""
                {
                  "amount": %d,
                  "ccy": 980,
                  "redirectUrl": "%s",
                  "webHookUrl": "%s",
                  "merchantPaymInfo": {
                    "reference": "%s",
                    "destination": "Оплата заказа #%s"
                  }
                }
                """, order.getTotal().multiply(BigDecimal.valueOf(100)).intValue(),
                    returnUrl, // Используем параметр returnUrl
                    cancelUrl, // Используем параметр cancelUrl для webhook (или создайте отдельный webhook URL)
                    order.getId(), order.getId());


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(MONOBANK_URL))
                    .header("X-Token", apiToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject json = new JSONObject(response.body());
            String paymentLink = json.getString("pageUrl");
            String invoiceId = json.getString("invoiceId");

            // Создаём и сохраняем Payment
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setAmount(order.getTotal());
            payment.setPaymentMethod(PaymentMethod.MONOBANK);
            payment.setTransactionId(invoiceId);
            payment.setStatus(PaymentStatus.PENDING);
            payment.setPaymentUrl(paymentLink);
            // Сохраняем ссылку на оплату в отдельном поле (нужно добавить это поле в сущность Payment)
            // payment.setPaymentUrl(paymentLink);

            paymentRepository.save(payment);

            // Можно вернуть объект, дополнительно передав ссылку клиенту (например, через DTO)
            return payment;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании Monobank платежа", e);
        }
    }

    @Override
    public Payment completePayment(String paymentId, String payerId) {
        // Не используется — статус придёт через webhook
        return null;
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Платёж не найден"));
    }

    @Override
    public String getSignature(String data) {
        // Monobank не использует подписи
        return null;
    }

    public Payment findByTransactionId(String invoiceId) {
        return paymentRepository.findByTransactionId(invoiceId);
    }
}