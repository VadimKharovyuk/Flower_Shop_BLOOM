package com.example.flowershoptr.config;

import com.example.flowershoptr.dto.category.CategoryListDTO;
import com.example.flowershoptr.enums.PaymentMethod;
import com.example.flowershoptr.enums.PaymentStatus;
import com.example.flowershoptr.service.CartService;
import com.example.flowershoptr.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice()
@RequiredArgsConstructor
@Slf4j
public class GlobalControllerAdvice {

    private final CartService cartService; // Предполагаем, что у вас есть такой сервис



    @ModelAttribute("currentUri")
    public String getCurrentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    /**
     * Добавляет количество товаров в корзине во все модели
     */
    @ModelAttribute("cartCount")
    public Integer getCartCount(HttpSession session) {
        try {
            return cartService.getCartItemCount(session);
        } catch (Exception e) {
            log.error("Ошибка при получении количества товаров в корзине", e);
            return 0;
        }
    }

    @ModelAttribute("PaymentMethod")
    public PaymentMethod[] paymentMethods() {
        return PaymentMethod.values();
    }

    @ModelAttribute("PaymentStatus")
    public PaymentStatus[] paymentStatuses() {
        return PaymentStatus.values();
    }

    /**
     * Добавляет переменную текущего года для футера
     */
    @ModelAttribute("currentYear")
    public int getCurrentYear() {
        return java.time.Year.now().getValue();
    }
}