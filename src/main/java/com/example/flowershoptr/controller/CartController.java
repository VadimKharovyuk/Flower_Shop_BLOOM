package com.example.flowershoptr.controller;

import com.example.flowershoptr.dto.cart.CartDto;
import com.example.flowershoptr.model.Order;
import com.example.flowershoptr.service.CartService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@RequiredArgsConstructor
@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
//    private final OrderService orderService;



    /**
     * Отображение страницы корзины
     */
    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        CartDto cart = cartService.getCartDto(session);
        model.addAttribute("cart", cart);
        return "client/cart/view";
    }

    /**
     * Добавление цветка в корзину
     */
    @PostMapping("/add")
    public String addToCart(
            HttpSession session,
            @RequestParam Long flowerId,
            @RequestParam(defaultValue = "1") Integer quantity,
            @RequestParam(required = false) String redirectUrl,
            RedirectAttributes redirectAttributes) {

        try {
            cartService.addFlowerToCart(session, flowerId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Товар добавлен в корзину");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        // Если указан URL для редиректа, используем его
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            return "redirect:" + redirectUrl;
        }

        return "redirect:/cart";
    }

    /**
     * Обновление количества цветка в корзине
     */
    @PostMapping("/update")
    public String updateCartItem(
            HttpSession session,
            @RequestParam Long flowerId,
            @RequestParam Integer quantity,
            RedirectAttributes redirectAttributes) {

        try {
            cartService.updateCartItemQuantity(session, flowerId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Количество товара обновлено");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/cart";
    }

    /**
     * Удаление цветка из корзины
     */
    @PostMapping("/remove")
    public String removeFromCart(
            HttpSession session,
            @RequestParam Long flowerId,
            RedirectAttributes redirectAttributes) {

        try {
            cartService.removeFlowerFromCart(session, flowerId);
            redirectAttributes.addFlashAttribute("successMessage", "Товар удален из корзины");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/cart";
    }

    /**
     * Очистка корзины
     */
    @PostMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        cartService.clearCart(session);
        redirectAttributes.addFlashAttribute("successMessage", "Корзина очищена");
        return "redirect:/cart";
    }

    /**
     * Отображение страницы оформления заказа
     */
    @GetMapping("/checkout")
    public String viewCheckout(HttpSession session, Model model) {
        CartDto cart = cartService.getCartDto(session);

        // Проверяем, что корзина не пуста
        if (cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        model.addAttribute("cart", cart);
        return "client/cart/checkout";
    }

    /**
     * Обработка формы оформления заказа
     */
    @PostMapping("/checkout")
    public String processCheckout(
            HttpSession session,
            @RequestParam String clientName,
            @RequestParam String clientPhone,
            @RequestParam String clientEmail,
            @RequestParam String deliveryAddress,
            @RequestParam String paymentMethod,
            RedirectAttributes redirectAttributes) {

        try {
            // Создаем заказ из корзины
            Order order = cartService.createOrderFromCart(session, clientName, clientPhone,
                    clientEmail, deliveryAddress, paymentMethod);

//            // Сохраняем заказ
//            Order savedOrder = orderService.saveOrder(order);
//
//            // Добавляем ID заказа для использования на странице подтверждения
//            redirectAttributes.addAttribute("orderId", savedOrder.getId());

            return "redirect:/order/confirmation";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/cart/checkout";
        }
    }

    /**
     * AJAX-метод для получения текущей корзины в формате JSON
     */
    @GetMapping("/api/info")
    @ResponseBody
    public CartDto getCartInfo(HttpSession session) {
        return cartService.getCartDto(session);
    }

    /**
     * AJAX-метод для добавления товара в корзину
     */
    @PostMapping("/api/add")
    @ResponseBody
    public CartDto apiAddToCart(
            HttpSession session,
            @RequestParam Long flowerId,
            @RequestParam(defaultValue = "1") Integer quantity) {

        return cartService.addFlowerToCart(session, flowerId, quantity);
    }
}