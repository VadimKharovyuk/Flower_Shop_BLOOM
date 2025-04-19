package com.example.flowershoptr.controller;
import com.example.flowershoptr.model.User;
import com.example.flowershoptr.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@RequestMapping("/aut")
@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "client/OAuth2User/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal != null) {
            String googleId = principal.getAttribute("sub");

            // Получаем пользователя из базы данных
            User user = userService.getUserByEmail(googleId);

            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("name", user.getName());
                model.addAttribute("email", user.getEmail());
                // Можно добавить другие атрибуты пользователя
            } else {
                // На случай, если пользователь не найден в базе
                model.addAttribute("name", principal.getAttribute("name"));
                model.addAttribute("email", principal.getAttribute("email"));
            }
        }
        return "client/OAuth2User/dashboard";
    }
}