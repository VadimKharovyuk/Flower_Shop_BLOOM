package com.example.flowershoptr.controller.admin;

import com.example.flowershoptr.dto.Order.OrderDetailsDTO;
import com.example.flowershoptr.dto.Order.OrderListDTO;
import com.example.flowershoptr.enums.OrderStatus;
import com.example.flowershoptr.enums.PaymentStatus;
import com.example.flowershoptr.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    @Autowired
    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Список всех заказов
    @GetMapping
    public String listOrders(Model model) {
        List<OrderListDTO> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin/orders/list";
    }

    // Фильтрация заказов по статусу
    @GetMapping("/filter")
    public String filterOrders(@RequestParam OrderStatus status, Model model) {
        List<OrderListDTO> orders = orderService.getOrdersByStatus(status);
        model.addAttribute("orders", orders);
        model.addAttribute("currentStatus", status);
        return "admin/orders/list";
    }

    // Просмотр деталей заказа
    @GetMapping("/{orderId}")
    public String viewOrder(@PathVariable Long orderId, Model model) {
        try {
            OrderDetailsDTO orderDetails = orderService.getOrderDetails(orderId);
            model.addAttribute("order", orderDetails);
            return "admin/orders/details";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Заказ не найден");
            return "error";
        }
    }

    // Обновление статуса заказа
    @PostMapping("/{orderId}/status")
    public String updateOrderStatus(@PathVariable Long orderId,
                                    @RequestParam OrderStatus status,
                                    Model model) {
        try {
            orderService.updateOrderStatus(orderId, status);
            return "redirect:/admin/orders/" + orderId + "?statusUpdated=true";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при обновлении статуса: " + e.getMessage());
            return "admin/orders/details";
        }
    }

    // Обновление статуса оплаты
    @PostMapping("/{orderId}/payment")
    public String updatePaymentStatus(@PathVariable Long orderId,
                                      @RequestParam PaymentStatus status,
                                      Model model) {
        try {
            orderService.updatePaymentStatus(orderId, status);
            return "redirect:/admin/orders/" + orderId + "?paymentUpdated=true";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при обновлении статуса оплаты: " + e.getMessage());
            return "admin/orders/details";
        }
    }

    // Отмена заказа
    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId, Model model) {
        try {
            orderService.cancelOrder(orderId);
            return "redirect:/admin/orders/" + orderId + "?canceled=true";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при отмене заказа: " + e.getMessage());
            return "admin/orders/details";
        }
    }
}