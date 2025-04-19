package com.example.flowershoptr.config;

import com.example.flowershoptr.model.User;
import com.example.flowershoptr.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.Collection;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Autowired
    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        logger.info("Loading OAuth user with email: {}", email);

        // Получаем пользователя из базы данных
        User user = userService.getUserByEmail(email);

        if (user == null) {
            String googleId = oauth2User.getAttribute("sub");
            user = userService.getUserByGoogleId(googleId);
            logger.info("User found by googleId: {}", user != null);
        }

        Collection<GrantedAuthority> authorities = new ArrayList<>(oauth2User.getAuthorities());

        // Добавляем роль из нашей базы данных
        if (user != null && user.getRole() != null) {
            // Предполагаем, что user.getRole() возвращает Enum типа UserRole
            String roleName = "ROLE_" + user.getRole().name();
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleName);
            authorities.add(authority);
            logger.info("Added authority from database: {}", authority.getAuthority());
        }

        return new DefaultOAuth2User(
                authorities,
                oauth2User.getAttributes(),
                userRequest.getClientRegistration().getProviderDetails()
                        .getUserInfoEndpoint().getUserNameAttributeName()
        );
    }
}