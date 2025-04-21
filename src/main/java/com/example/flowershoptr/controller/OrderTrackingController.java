package com.example.flowershoptr.controller;

import com.example.flowershoptr.dto.Order.OrderDetailsDTO;
import com.example.flowershoptr.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/order-tracking")
@RequiredArgsConstructor
public class OrderTrackingController {
    private final OrderService orderService;


    @GetMapping
    public String orderTracking() {


        return "client/checkout/order-tracking";
    }



    // Обработка поиска по номеру заказа
    @GetMapping("/by-number")
    public String trackOrderByNumber(@RequestParam("orderNumber") String orderNumber, Model model) {
        Optional<OrderDetailsDTO> optionalOrder = orderService.getOrderByNumber(orderNumber);

        if (optionalOrder.isPresent()) {
            OrderDetailsDTO order = optionalOrder.get();
            model.addAttribute("order", order);
            return "client/checkout/order-details";
        } else {
            model.addAttribute("errorMessage", "Заказ с указанным номером не найден");
            return "client/checkout/order-tracking";
        }
    }
    // Обработка поиска по номеру телефона
    @GetMapping("/by-phone")
    public String trackOrderByPhone(@RequestParam("phone") String phone, Model model) {
        try {
            List<OrderDetailsDTO> orders = orderService.getOrdersByPhone(phone);

            if (orders.isEmpty()) {
                model.addAttribute("errorMessage", "Заказы с указанным номером телефона не найдены");
                return "client/checkout/order-tracking";
            } else if (orders.size() == 1) {
                // Если найден только один заказ, сразу показываем его детали
                model.addAttribute("order", orders.get(0));
                return "client/checkout/order-details";
            } else {
                // Если найдено несколько заказов, показываем список для выбора
                model.addAttribute("orders", orders);
                return "client/checkout/order-list";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при поиске заказов: " + e.getMessage());
            return "client/checkout/order-tracking";
        }
    }

    // Комбинированный поиск (форма POST)
    @PostMapping("/search")
    public String searchOrder(@RequestParam(value = "orderNumber", required = false) String orderNumber,
                              @RequestParam(value = "phone", required = false) String phone,
                              Model model) {
        try {
            // Если указан ID заказа, ищем по нему
            if (orderNumber != null) {
                return "redirect:/order-tracking/by-id?orderId=" + orderNumber;
            }

            // Если указан телефон, ищем по нему
            if (phone != null && !phone.trim().isEmpty()) {
                return "redirect:/order-tracking/by-phone?phone=" +
                        URLEncoder.encode(phone.trim(), StandardCharsets.UTF_8);
            }

            // Если не указаны ни ID, ни телефон
            model.addAttribute("errorMessage", "Пожалуйста, укажите номер заказа или номер телефона");
            return "client/checkout/order-tracking";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при поиске заказа: " + e.getMessage());
            return "client/checkout/order-tracking";
        }
    }
}
