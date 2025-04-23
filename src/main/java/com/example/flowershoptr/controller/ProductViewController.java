package com.example.flowershoptr.controller;

import com.example.flowershoptr.dto.flower.FlowerDetailsDTO;
import com.example.flowershoptr.model.Flower;
import com.example.flowershoptr.model.UserProductView;
import com.example.flowershoptr.service.FlowerService;
import com.example.flowershoptr.service.ProductViewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/view")
public class ProductViewController {

    private final ProductViewService productViewService;
    private final FlowerService flowerService;



    @GetMapping("/recently-viewed")
    public String recentlyViewed(HttpSession session, Authentication authentication, Model model) {
        List<UserProductView> views;

        if (authentication != null && authentication.isAuthenticated()) {
            // Для авторизованного пользователя
            String email = ((OAuth2User) authentication.getPrincipal()).getAttribute("email");
            views = productViewService.getUserViewsByEmail(email);
        } else {
            // Для неавторизованного пользователя
            views = productViewService.getViewsForSession(session.getId());
        }

        List<FlowerDetailsDTO> products = views.stream()
                .map(view -> flowerService.getFlowerById(view.getProductId()))
                .collect(Collectors.toList());

        model.addAttribute("recentProducts", products);
        log.info("size = " + products.size());
        return "client/view/recently-viewed";
    }

    // Обработчик успешной авторизации
    @GetMapping("/login/success")
    public String loginSuccess(HttpSession session, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = ((OAuth2User) authentication.getPrincipal()).getAttribute("email");

            // Перенос просмотров из сессии в БД
            productViewService.transferSessionViewsToUserByEmail(session.getId(), email);
        }

        return "redirect:/";
    }
}
