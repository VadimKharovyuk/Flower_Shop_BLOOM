package com.example.flowershoptr.controller;

import com.example.flowershoptr.dto.Order.OrderDetailsDTO;
import com.example.flowershoptr.dto.Order.OrderListDTO;
import com.example.flowershoptr.model.User;
import com.example.flowershoptr.service.AuthService;
import com.example.flowershoptr.service.OrderService;
import com.example.flowershoptr.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/profile/orders")
public class ProfileController {

    private final OrderService orderService;
    private final AuthService authService;


    @GetMapping()
    public String viewOrders(Model model, Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        if (user == null) {
            return "redirect:/aut/login";
        }
        model.addAttribute("user", user);
        List<OrderListDTO> orders = orderService.getAllOrdersByUserIdOrEmail(user.getId(), user.getEmail());
        model.addAttribute("orders", orders);

        return "client/profile/orders";
    }
    @GetMapping("/{orderId}")
    public String viewOrderDetails(@PathVariable Long orderId, Model model, Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        if (user == null) {
            return "redirect:/aut/login";
        }

        OrderDetailsDTO orderDetails = orderService.getOrderDetailsByIdAndUserIdOrEmail(orderId, user.getId(), user.getEmail());
        if (orderDetails != null) {
            model.addAttribute("order", orderDetails);
            return "client/profile/order-details";
        }
        return "redirect:/aut/login";//
    }

}