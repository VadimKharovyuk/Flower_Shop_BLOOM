//package com.example.flowershoptr.service.serviceImpl;
//
//import com.example.flowershoptr.enums.PaymentStatus;
//import com.example.flowershoptr.model.Order;
//import com.example.flowershoptr.model.Payment;
//import com.example.flowershoptr.repository.PaymentRepository;
//import com.example.flowershoptr.service.PaymentService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class PayPalPaymentServiceImpl implements PaymentService {
//
//    @Value("${paypal.client-id}")
//    private String clientId;
//
//    @Value("${paypal.client-secret}")
//    private String clientSecret;
//
//    @Value("${paypal.mode}")
//    private String mode;
//
//    @Autowired
//    private PaymentRepository paymentRepository;
//
//    @Override
//    public Payment processPayment(Order order, String returnUrl, String cancelUrl) {
//        try {
//            // Создаем объект платежа
//            Payment payment = new Payment();
//            payment.setOrder(order);
//            payment.setAmount(order.getTotal());
//            payment.setPaymentMethod(order.getPaymentMethod());
//            payment.setStatus(PaymentStatus.PENDING);
//            payment = paymentRepository.save(payment);
//
//            // Создаем PayPal платеж
//            APIContext apiContext = new APIContext(clientId, clientSecret, mode);
//
//            // Создаем список товаров
//            List<Item> items = new ArrayList<>();
//            for (com.example.flowershoptr.model.OrderItem item : order.getItems()) {
//                Item paypalItem = new Item();
//                paypalItem.setName(item.getFlowerName());
//                paypalItem.setPrice(item.getPrice().toString());
//                paypalItem.setCurrency("USD");
//                paypalItem.setQuantity(String.valueOf(item.getQuantity()));
//                items.add(paypalItem);
//            }
//
//            // Создаем детали платежа
//            ItemList itemList = new ItemList();
//            itemList.setItems(items);
//
//            // Сумма заказа
//            Details details = new Details();
//            details.setSubtotal(order.getTotal().toString());
//            details.setShipping("0");
//            details.setTax("0");
//
//            Amount amount = new Amount();
//            amount.setCurrency("USD");
//            amount.setTotal(order.getTotal().toString());
//            amount.setDetails(details);
//
//            Transaction transaction = new Transaction();
//            transaction.setAmount(amount);
//            transaction.setDescription("Заказ цветов #" + order.getOrderNumber());
//            transaction.setItemList(itemList);
//
//            List<Transaction> transactions = new ArrayList<>();
//            transactions.add(transaction);
//
//            // Создаем платеж
//            com.paypal.api.payments.Payment paypalPayment = new com.paypal.api.payments.Payment();
//            paypalPayment.setIntent("sale");
//
//            // Настройка Payer
//            Payer payer = new Payer();
//            payer.setPaymentMethod("paypal");
//            paypalPayment.setPayer(payer);
//            paypalPayment.setTransactions(transactions);
//
//            // Настройка URL для редиректа
//            RedirectUrls redirectUrls = new RedirectUrls();
//            redirectUrls.setReturnUrl(returnUrl);
//            redirectUrls.setCancelUrl(cancelUrl);
//            paypalPayment.setRedirectUrls(redirectUrls);
//
//            // Создаем платеж в PayPal
//            com.paypal.api.payments.Payment createdPayment = paypalPayment.create(apiContext);
//
//            // Сохраняем ID транзакции
//            payment.setTransactionId(createdPayment.getId());
//
//            // Получаем ссылку для перенаправления
//            String redirectUrl = createdPayment.getLinks().stream()
//                    .filter(link -> link.getRel().equals("approval_url"))
//                    .findFirst()
//                    .orElseThrow(() -> new PayPalRESTException("No approval URL found"))
//                    .getHref();
//
//            // Здесь можно сохранить URL в payment, если необходимо
//
//            return paymentRepository.save(payment);
//
//        } catch (PayPalRESTException e) {
//            throw new RuntimeException("Ошибка при создании платежа PayPal: " + e.getMessage(), e);
//        }
//    }
//
//    @Override
//    public Payment completePayment(String paymentId, String payerId) {
//        try {
//            // Находим платеж по ID транзакции
//            Payment payment = paymentRepository.findByTransactionId(paymentId);
//
//            if (payment != null) {
//                // Создаем API контекст
//                APIContext apiContext = new APIContext(clientId, clientSecret, mode);
//
//                // Завершаем платеж в PayPal
//                com.paypal.api.payments.Payment paypalPayment = com.paypal.api.payments.Payment.get(apiContext, paymentId);
//
//                PaymentExecution paymentExecution = new PaymentExecution();
//                paymentExecution.setPayerId(payerId);
//
//                com.paypal.api.payments.Payment executedPayment = paypalPayment.execute(apiContext, paymentExecution);
//
//                if (executedPayment.getState().equals("approved")) {
//                    payment.setStatus(PaymentStatus.PAID);
//                    payment.setPaidAt(LocalDateTime.now());
//
//                    // Обновляем статус заказа
//                    Order order = payment.getOrder();
//                    order.setPaymentStatus(PaymentStatus.PAID);
//
//                    return paymentRepository.save(payment);
//                }
//            }
//
//            return null;
//        } catch (PayPalRESTException e) {
//            throw new RuntimeException("Ошибка при завершении платежа PayPal: " + e.getMessage(), e);
//        }
//    }
//
//    @Override
//    public Payment getPaymentById(Long id) {
//        return paymentRepository.findById(id).orElse(null);
//    }
//}