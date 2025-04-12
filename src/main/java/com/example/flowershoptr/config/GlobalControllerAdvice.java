package com.example.flowershoptr.config;

import com.example.flowershoptr.dto.category.CategoryListDTO;
import com.example.flowershoptr.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice(basePackages = "com.example.flowershoptr.controller.client")
@RequiredArgsConstructor
@Slf4j
public class GlobalControllerAdvice {

    private final CategoryService categoryService;
//    private final CartService cartService; // Предполагаем, что у вас есть такой сервис

    /**
     * Добавляет список избранных категорий во все модели
     */
    @ModelAttribute("featuredCategories")
    public List<CategoryListDTO> getFeaturedCategories() {
        log.debug("Загрузка избранных категорий для глобального использования");
        PageRequest pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.ASC, "name"));
        Page<CategoryListDTO> categories = categoryService.getFeaturedCategories(pageable);
        return categories.getContent();
    }

    /**
     * Добавляет количество товаров в корзине во все модели
     */
//    @ModelAttribute("cartCount")
//    public Integer getCartCount() {
//        try {
//            return cartService.getCartItemCount();
//        } catch (Exception e) {
//            log.error("Ошибка при получении количества товаров в корзине", e);
//            return 0;
//        }
//    }

    /**
     * Добавляет переменную текущего года для футера
     */
    @ModelAttribute("currentYear")
    public int getCurrentYear() {
        return java.time.Year.now().getValue();
    }
}