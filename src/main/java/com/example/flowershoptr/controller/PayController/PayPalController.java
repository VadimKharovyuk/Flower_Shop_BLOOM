package com.example.flowershoptr.controller.PayController;

import com.example.flowershoptr.enums.OrderStatus;
import com.example.flowershoptr.enums.PaymentStatus;
import com.example.flowershoptr.model.Order;
import com.example.flowershoptr.model.Payment;
import com.example.flowershoptr.repository.PaymentRepository;
import com.example.flowershoptr.service.OrderService;
import com.example.flowershoptr.service.payment.PayPalService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@AllArgsConstructor
@Controller
@RequestMapping("/payment")
public class PayPalController {

    private final OrderService orderService;
    private final PayPalService payPalService;
    private final PaymentRepository paymentRepository;

    @GetMapping("/paypal/{orderId}")
    public String initiatePayPalPayment(@PathVariable Long orderId, Model model) {
        try {
            Order order = orderService.getOrderById(orderId);
            Payment payment = payPalService.createPayment(order);
            return "redirect:" + payment.getPaymentUrl();
        } catch (IOException e) {
            model.addAttribute("error", "Failed to create PayPal payment: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/paypal/success")
    public String paymentSuccess(@RequestParam("token") String token, Model model) {
        try {
            Payment payment = payPalService.capturePayment(token);
            model.addAttribute("payment", payment);
            return "client/PayPal/payment-success";
        } catch (IOException e) {
            model.addAttribute("error", "Failed to capture PayPal payment: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/paypal/cancel")
    public String paymentCancel() {
        return "client/PayPal/Payment-cancel";
    }

    @GetMapping("/status/{orderId}")
    public String checkPaymentStatus(@PathVariable Long orderId, Model model) {
        try {
            Order order = orderService.getOrderById(orderId);
            Payment payment = paymentRepository.findByOrder(order);

            if (payment != null) {
                model.addAttribute("payment", payment);
                model.addAttribute("order", order);
                return "client/PayPal/status";
            } else {
                model.addAttribute("error", "Платеж для заказа не найден");
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при получении статуса платежа: " + e.getMessage());
            return "error";
        }
    }
}