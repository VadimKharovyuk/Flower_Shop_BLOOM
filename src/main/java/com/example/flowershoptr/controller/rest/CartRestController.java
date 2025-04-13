package com.example.flowershoptr.controller.rest;

import com.example.flowershoptr.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class CartRestController {
    private final CartService cartService;

    @PostMapping("/api/add")
    @ResponseBody
    public Map<String, Object> apiAddToCart(
            HttpSession session,
            @RequestParam Long flowerId,
            @RequestParam(defaultValue = "1") Integer quantity) {

        try {
            // Добавление товара в корзину
            cartService.addFlowerToCart(session, flowerId, quantity);

            // Возвращаем успешный ответ с сообщением
            return Map.of("success", true, "message", "Товар добавлен в корзину");
        } catch (Exception e) {
            // В случае ошибки возвращаем сообщение об ошибке
            return Map.of("success", false, "message", e.getMessage());
        }
    }
}