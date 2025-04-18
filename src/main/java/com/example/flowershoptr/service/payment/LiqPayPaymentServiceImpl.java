package com.example.flowershoptr.service.payment;

import com.example.flowershoptr.enums.PaymentStatus;
import com.example.flowershoptr.model.Order;
import com.example.flowershoptr.model.Payment;
import com.example.flowershoptr.repository.PaymentRepository;
import com.example.flowershoptr.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class LiqPayPaymentServiceImpl implements PaymentService {

    @Value("${liqpay.public_key}")
    private String publicKey;

    @Value("${liqpay.private_key}")
    private String privateKey;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Payment processPayment(Order order, String returnUrl, String cancelUrl) {
        // Создаем объект платежа
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotal());
        payment.setPaymentMethod(order.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);
        payment = paymentRepository.save(payment);

        // Формируем данные для LiqPay
        Map<String, Object> params = new HashMap<>();
        params.put("action", "pay");
        params.put("amount", order.getTotal().toString());
        params.put("currency", "UAH");
        params.put("description", "Заказ цветов #" + order.getOrderNumber());
        params.put("order_id", order.getOrderNumber());
        params.put("version", "3");
        params.put("public_key", publicKey);
        params.put("result_url", returnUrl);
        params.put("server_url", returnUrl);

        // Создаем JSON из параметров
        JSONObject jsonParams = new JSONObject(params);
        String jsonString = jsonParams.toString();

        // Кодируем данные
        String data = Base64.getEncoder().encodeToString(jsonString.getBytes(StandardCharsets.UTF_8));

        // Создаем подпись
        String signature = getSignature(data);

        // Сохраняем данные для формы
        payment.setTransactionId(data);

        return paymentRepository.save(payment);
    }

    @Override
    public Payment completePayment(String paymentId, String payerId) {
        // Находим платеж по ID транзакции
        Payment payment = paymentRepository.findByTransactionId(paymentId);

        if (payment != null) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaidAt(LocalDateTime.now());

            // Обновляем статус заказа
            Order order = payment.getOrder();
            order.setPaymentStatus(PaymentStatus.COMPLETED);

            return paymentRepository.save(payment);
        }

        return null;
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    // Метод для создания подписи
    @Override
    public String getSignature(String data) {
        String signString = privateKey + data + privateKey;
        return Base64.getEncoder().encodeToString(
                org.apache.commons.codec.digest.DigestUtils.sha1(signString));
    }



}