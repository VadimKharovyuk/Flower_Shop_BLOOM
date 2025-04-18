//package com.example.flowershoptr.controller.admin;
//
//import com.example.flowershoptr.enums.PaymentStatus;
//import com.example.flowershoptr.model.Event;
//import com.example.flowershoptr.service.OrderService;
//import jakarta.mail.Session;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/payment-webhook")
//public class PaymentWebhookController {
//
//    private final OrderService orderService;
//    private final StripeService stripeService;
//
//    @Autowired
//    public PaymentWebhookController(OrderService orderService, StripeService stripeService) {
//        this.orderService = orderService;
//        this.stripeService = stripeService;
//    }
//
//    @PostMapping
//    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
//                                                      @RequestHeader("Stripe-Signature") String signature) {
//        try {
//            // Проверяем подпись и обрабатываем событие
//            Event event = stripeService.constructEventFromPayload(payload, signature);
//
//            // Обрабатываем разные типы событий
//            if ("checkout.session.completed".equals(event.getType())) {
//                Session session = (Session) event.getDataObjectDeserializer().getObject().get();
//                Long orderId = Long.valueOf(session.getClientReferenceId());
//
//                // Обновляем статус заказа
//                orderService.updatePaymentStatus(orderId, PaymentStatus.COMPLETED);
//            }
//
//            return ResponseEntity.ok("Webhook received");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error: " + e.getMessage());
//        }
//    }
//}