package com.example.flowershoptr.controller;

import com.example.flowershoptr.dto.Order.CreateOrderDTO;
import com.example.flowershoptr.dto.Order.OrderDetailsDTO;
import com.example.flowershoptr.dto.cart.CartDto;
import com.example.flowershoptr.enums.OrderStatus;
import com.example.flowershoptr.enums.PaymentStatus;
import com.example.flowershoptr.model.Cart;
import com.example.flowershoptr.model.Order;
import com.example.flowershoptr.service.CartService;
import com.example.flowershoptr.service.NotificationEmailService;
import com.example.flowershoptr.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final OrderService orderService;
    private final CartService cartService;
    private final NotificationEmailService notificationEmailService;

    @GetMapping
    public String showCheckoutForm(Model model , HttpSession session) {
        model.addAttribute("orderDTO", new CreateOrderDTO());

        CartDto cart = cartService.getCartDto(session);
        model.addAttribute("item", cart);

        return "client/checkout/form";
    }

    // Обработка отправки формы
    @PostMapping
    public String processOrder(@Valid @ModelAttribute("orderDTO") CreateOrderDTO orderDTO,
                               BindingResult bindingResult,
                               HttpSession session,
                               Model model) {
        // Проверка на ошибки валидации
        if (bindingResult.hasErrors()) {
            return "client/checkout/form";
        }

        try {
            // Создаем заказ
            Order order = orderService.createOrder(orderDTO, session);


            // Перенаправляем на страницу подтверждения заказа
            return "redirect:/checkout/confirmation/" + order.getId();
        } catch (Exception e) {
            // В случае ошибки добавляем сообщение об ошибке и возвращаемся к форме
            model.addAttribute("errorMessage", "Ошибка при оформлении заказа: " + e.getMessage());
            return "client/checkout/form";
        }
    }


    // Страница подтверждения заказа
    @GetMapping("/confirmation/{orderId}")
    public String orderConfirmation(@PathVariable Long orderId, Model model) {
        try {
            OrderDetailsDTO orderDetails = orderService.getOrderDetails(orderId);
            model.addAttribute("order", orderDetails); // Передаем весь объект OrderDetailsDTO
            return "client/checkout/confirmation";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Заказ не найден");
            return "error";
        }
    }

    // Отображение деталей заказа для клиента
    @GetMapping("/order/{orderId}")
    public String viewOrder(@PathVariable Long orderId, Model model) {
        try {
            OrderDetailsDTO orderDetails = orderService.getOrderDetails(orderId);
            model.addAttribute("order", orderDetails);
            return "client/checkout/order-details";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Заказ не найден");
            return "error";
        }
    }


    @PostMapping("/order/{orderId}/send-email")
    public String sendOrderEmail(@PathVariable Long orderId,
                                 @RequestParam(required = false) String email,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Если email передан в запросе, нужно обновить его в заказе перед отправкой
            if (email != null && !email.isEmpty()) {
                 orderService.updateOrderEmail(orderId, email);
            }

            boolean sent = notificationEmailService.sendOrderConfirmationEmail(orderId);

            if (sent) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Информация о заказе успешно отправлена на вашу почту");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Не удалось отправить информацию о заказе. Пожалуйста, проверьте правильность email");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Произошла ошибка при отправке заказа: " + e.getMessage());
        }

        return "redirect:/checkout/order/" + orderId;
    }

    // Отмена заказа
    @PostMapping("/order/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId, Model model) {
        try {
            orderService.cancelOrder(orderId);
            return "redirect:/checkout/order/" + orderId + "?canceled=true";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при отмене заказа: " + e.getMessage());
            return "client/checkout/order-details";
        }
    }

    // Обработка оплаты (простой пример)
    @PostMapping("/order/{orderId}/pay")
    public String payOrder(@PathVariable Long orderId, Model model,HttpSession session) {
        try {

            Order order = orderService.updatePaymentStatus(orderId, PaymentStatus.COMPLETED);
          cartService.clearCart(session);
            return "redirect:/checkout/order/" + orderId + "?paid=true";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при оплате заказа: " + e.getMessage());
            return "client/checkout/order-details";
        }
    }
}