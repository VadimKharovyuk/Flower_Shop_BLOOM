package com.example.flowershoptr.controller;

import com.example.flowershoptr.dto.ProductReview.CreateProductReviewDTO;
import com.example.flowershoptr.dto.ProductReview.ProductReviewDTO;
import com.example.flowershoptr.dto.ProductReview.ProductReviewSummaryDTO;
import com.example.flowershoptr.model.User;
import com.example.flowershoptr.service.AuthService;
import com.example.flowershoptr.service.FlowerService;
import com.example.flowershoptr.service.ProductReviewService;
import com.example.flowershoptr.service.UserService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/product-review")
@RequiredArgsConstructor
public class ProductReviewController {


    private final ProductReviewService productReviewService;
    private final AuthService authService;
    private final FlowerService flowerService;


    /**
     * Отображение отзывов для конкретного цветка
     */
    @GetMapping("/flower/{flowerId}")
    public String showFlowerReviews(
            @PathVariable Long flowerId,
            Model model,
            Authentication authentication
    ) {
        // Получаем отзывы и статистику
        List<ProductReviewDTO> reviews = productReviewService.getReviewsByFlowerId(flowerId);
        ProductReviewSummaryDTO summary = productReviewService.getReviewSummaryByFlowerId(flowerId);

        // Получаем текущего пользователя, если авторизован
        User currentUser = authService.getCurrentUser(authentication);

        // Флаги для условий отображения формы
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        boolean canReview = false;

        if (isAuthenticated) {
            // Проверяем возможность оставить отзыв
            canReview = productReviewService.canUserReviewProduct(currentUser.getId(), flowerId);

            // Создаем DTO для формы отзыва
            CreateProductReviewDTO createReviewDTO = new CreateProductReviewDTO();
            createReviewDTO.setFlowerId(flowerId);
            model.addAttribute("createReviewDTO", createReviewDTO);
        }

        // Добавляем атрибуты в модель
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("flower", flowerService.getFlowerById(flowerId));
        model.addAttribute("reviews", reviews);
        model.addAttribute("summary", summary);
        model.addAttribute("canReview", canReview);

        return "client/product-review/reviews";

    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createReview(
            @Valid @RequestBody CreateProductReviewDTO createDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal OAuth2User oauth2User) {

        // Создаем карту для ответа
        Map<String, Object> response = new HashMap<>();

        try {
            // Извлечение email из OAuth2 аутентификации
            String email = oauth2User.getAttribute("email");

            // Проверка валидации
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getAllErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.toList());

                response.put("success", false);
                response.put("errors", errors);
                return ResponseEntity.badRequest().body(response);
            }

            // Создание отзыва через сервис
            ProductReviewDTO createdReview = productReviewService.createProductReview(createDTO, email);

            response.put("success", true);
            response.put("message", "Отзыв успешно добавлен");
            response.put("review", createdReview);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
//
//    @PostMapping("/create")
//    public String createReview(
//            @Valid @ModelAttribute CreateProductReviewDTO createDTO,
//            BindingResult bindingResult,
//            @AuthenticationPrincipal OAuth2User oauth2User,
//            RedirectAttributes redirectAttributes) {
//
//        try {
//            // Извлечение email из OAuth2 аутентификации
//            String email = oauth2User.getAttribute("email");
//
//            // Проверка валидации
//            if (bindingResult.hasErrors()) {
//                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.createReviewDTO", bindingResult);
//                redirectAttributes.addFlashAttribute("createReviewDTO", createDTO);
//                return "redirect:/flowers/" + createDTO.getFlowerId();
//            }
//
//
//            // Создание отзыва через сервис
//            ProductReviewDTO createdReview = productReviewService.createProductReview(createDTO, email);
//
//            redirectAttributes.addFlashAttribute("successMessage", "Отзыв успешно добавлен");
//            return "redirect:/flowers/" + createDTO.getFlowerId();
//
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
//            return "redirect:/flowers/" + createDTO.getFlowerId();
//        }
//    }

    /**
     * Класс для стандартизации ошибок API
     */
    @Getter
    @AllArgsConstructor
    public static class ErrorResponse {
        private String message;
    }

    /**
     * Просмотр отзывов текущего пользователя в личном кабинете
     */
    @GetMapping("/my-reviews")
    public String myReviews(Model model, Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        List<ProductReviewDTO> userReviews = productReviewService.getCurrentUserReviews(authentication);
        model.addAttribute("userReviews", userReviews);
        model.addAttribute("user", currentUser);

        return "client/profile/reviews";
    }

    /**
     * Удаление отзыва
     */
    @PostMapping("/delete/{reviewId}")
    public String deleteReview(@PathVariable Long reviewId,
                               RedirectAttributes redirectAttributes,
                               Authentication authentication) {

        User currentUser = authService.getCurrentUser(authentication);
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            // Получаем отзыв, чтобы знать ID цветка для редиректа
            ProductReviewDTO review = productReviewService.getById(reviewId);

            // Удаляем отзыв
            productReviewService.deleteProductReview(reviewId, currentUser);

            redirectAttributes.addFlashAttribute("success", "Отзыв успешно удален");

            // Редирект в зависимости от откуда пришел запрос
            String referer = "my-reviews"; // По умолчанию в личный кабинет
            return "redirect:/reviews/" + referer;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Произошла ошибка при удалении отзыва");
            return "redirect:/reviews/my-reviews";
        }
    }

    /**
     * AJAX обработчик для проверки, может ли пользователь оставить отзыв
     */
    @GetMapping("/api/can-review")
    @ResponseBody
    public boolean canReview(@RequestParam Long flowerId, Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication);
        if (currentUser == null) {
            return false;
        }
        return productReviewService.canUserReviewProduct(currentUser.getId(), flowerId);
    }
}