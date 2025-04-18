package com.example.flowershoptr.service;

import com.example.flowershoptr.dto.Order.OrderDetailsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEmailService {

    private final EmailService emailService;
    private final OrderService orderService;

    /**
     * Отправляет детали заказа на email клиента
     *
     * @param orderId ID заказа
     * @return true, если отправка прошла успешно
     */
    @Transactional(readOnly = true)
    public boolean sendOrderConfirmationEmail(Long orderId) {
        try {
            OrderDetailsDTO orderDetails = orderService.getOrderDetails(orderId);

            // Проверяем, указан ли email
            if (orderDetails.getClientEmail() == null || orderDetails.getClientEmail().isEmpty()) {
                log.warn("Не удалось отправить подтверждение заказа: email клиента не указан для заказа ID {}", orderId);
                return false;
            }

            // Создаем список с одним email-адресом
            List<String> recipients = new ArrayList<>();
            recipients.add(orderDetails.getClientEmail());

            Map<String, Object> context = new HashMap<>();
            context.put("message", "Ваш заказ №" + orderDetails.getId() + " успешно оформлен и принят в обработку.");
            context.put("subscriberCount", 1);

            // Добавляем информацию о заказе
            context.put("order", orderDetails);
            context.put("orderUrl", "/checkout/order/" + orderDetails.getId());

            log.info("Отправка подтверждения заказа на email: {}, заказ ID: {}",
                    orderDetails.getClientEmail(), orderDetails.getId());

            // Используем тот же шаблон, что и для кастомной рассылки
            emailService.sendBulkEmail(
                    recipients,
                    "Подтверждение заказа #" + orderDetails.getId(),
                    "order-confirmations",
                    context
            );

            return true;
        } catch (Exception e) {
            log.error("Ошибка при отправке подтверждения заказа ID {}: {}", orderId, e.getMessage(), e);
            return false;
        }
    }

}