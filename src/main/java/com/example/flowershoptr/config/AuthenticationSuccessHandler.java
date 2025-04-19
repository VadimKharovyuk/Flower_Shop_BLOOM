package com.example.flowershoptr.config;//package com.example.flowershoptr.config;
//
//import com.example.flowershoptr.model.User;
//import com.example.flowershoptr.service.UserService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.event.EventListener;
//import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Component;
//
//@Component
//public class AuthenticationSuccessHandler {
//
//    private static final Logger logger = LoggerFactory.getLogger(AuthenticationSuccessHandler.class);
//
//    private final UserService userService;
//
//    @Autowired
//    public AuthenticationSuccessHandler(UserService userService) {
//        this.userService = userService;
//    }
//
//    @EventListener
//    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
//        logger.info("Authentication success event triggered");
//
//        // Проверяем на OAuth2LoginAuthenticationToken вместо OAuth2AuthenticationToken
//        if (event.getAuthentication() instanceof OAuth2LoginAuthenticationToken) {
//            OAuth2LoginAuthenticationToken oauth2Token = (OAuth2LoginAuthenticationToken) event.getAuthentication();
//            OAuth2User oauth2User = oauth2Token.getPrincipal();
//            try {
//                User user = userService.processOAuthPostLogin(oauth2User);
//                logger.info("User saved/updated in database with ID: {}", user.getId());
//            } catch (Exception e) {
//                logger.error("Error saving user to database", e);
//            }
//        } else {
//            logger.info("Authentication is not OAuth2Login: {}", event.getAuthentication().getClass().getName());
//        }
//    }
//}

import com.example.flowershoptr.enums.UserRole;
import com.example.flowershoptr.model.User;
import com.example.flowershoptr.service.CartService;
import com.example.flowershoptr.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessHandler {
    private final UserService userService;


    @Autowired
    public AuthenticationSuccessHandler(UserService userService, CartService cartService, HttpSession httpSession) {
        this.userService = userService;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        if (event.getAuthentication() instanceof OAuth2LoginAuthenticationToken) {
            OAuth2LoginAuthenticationToken oauth2Token = (OAuth2LoginAuthenticationToken) event.getAuthentication();
            OAuth2User oauth2User = oauth2Token.getPrincipal();

            String email = oauth2User.getAttribute("email");
            String googleId = oauth2User.getAttribute("sub");

            User user = userService.getUserByGoogleId(googleId);
            if (user == null) {
                user = userService.getUserByEmail(email);
            }

            if (user != null) {
                // Если email администраторский, устанавливаем роль ADMIN
                if ("vadimkh17@gmail.com".equals(email)) {
                    user.setRole(UserRole.ADMIN);
                    userService.saveUser(user);
                }
            }
        }
    }
}