package com.example.flowershoptr.controller.admin;

import com.example.flowershoptr.enums.PaymentMethod;
import com.example.flowershoptr.model.Order;
import com.example.flowershoptr.model.Payment;
import com.example.flowershoptr.service.CartService;
import com.example.flowershoptr.service.OrderService;
import com.example.flowershoptr.service.serviceImpl.LiqPayPaymentServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final OrderService orderService;
    private final LiqPayPaymentServiceImpl liqPayPaymentService;
    private final CartService cartService;

    @GetMapping("/process/{orderId}")
    public String processPayment(@PathVariable Long orderId, HttpServletRequest request, Model model) {
        Order order = orderService.getOrderById(orderId);

        if (order == null) {
            return "redirect:/checkout?error=order-not-found";
        }

        // Формируем URL для возврата и отмены
        String baseUrl = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            baseUrl += ":" + request.getServerPort();
        }

        String returnUrl = baseUrl + "/payment/success";
        String cancelUrl = baseUrl + "/payment/cancel";

        Payment payment = liqPayPaymentService.processPayment(order, returnUrl, cancelUrl);

        // Для LiqPay нужно передать data и signature на страницу платежной формы
        model.addAttribute("paymentData", payment.getTransactionId());
        model.addAttribute("signature", liqPayPaymentService.getSignature(payment.getTransactionId()));
        model.addAttribute("order", order);

        return "client/payment/liqpay-form";
    }

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam(value = "payment_id", required = false) String paymentId,
                                 @RequestParam(value = "data", required = false) String data,
                                 HttpSession session,
                                 Model model) {

        if (data != null) {
            // Обработка ответа от LiqPay
            Payment payment = liqPayPaymentService.completePayment(data, null);

            if (payment != null) {
                // Очищаем корзину после успешной оплаты
                cartService.clearCart(session);
                return "redirect:/checkout/confirmation/" + payment.getOrder().getId() + "?payment_status=success";
            }
        }

        model.addAttribute("errorMessage", "Не удалось найти информацию о платеже");
        return "client/payment/error";
    }


    /**
     * Обрабатывает отмену платежа
     */
    @GetMapping("/cancel")
    public String paymentCancel(Model model) {
        model.addAttribute("message", "Платеж был отменен");
        return "client/payment/cancelled";
    }

    /**
     * Обрабатывает ошибки платежа
     */
    @GetMapping("/error")
    public String paymentError(@RequestParam(required = false) String message, Model model) {
        model.addAttribute("errorMessage", message != null ? message : "Произошла ошибка при обработке платежа");
        return "client/payment/error";
    }

    /**
     * Обработчик уведомлений от LiqPay (callback)
     */
    @PostMapping("/callback")
    @ResponseBody
    public String paymentCallback(@RequestParam("data") String data,
                                  @RequestParam("signature") String signature) {

        // Проверка подписи для безопасности
        String calculatedSignature = liqPayPaymentService.getSignature(data);

        if (calculatedSignature.equals(signature)) {
            // Подпись верна, обрабатываем платеж
            Payment payment = liqPayPaymentService.completePayment(data, null);

            if (payment != null) {
                return "ok";
            }
        }

        return "error";
    }
}