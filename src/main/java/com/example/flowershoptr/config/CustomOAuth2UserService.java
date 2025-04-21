package com.example.flowershoptr.config;

import com.example.flowershoptr.enums.UserRole;
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
        String googleId = oauth2User.getAttribute("sub");
        logger.info("Loading OAuth user with email: {}, googleId: {}", email, googleId);

        // Получаем пользователя из базы данных по email или googleId
        User user = userService.getUserByEmail(email);

        if (user == null) {
            user = userService.getUserByGoogleId(googleId);
            logger.info("User found by googleId: {}", user != null);
        }

        // Если пользователь не найден, создаем нового
        if (user == null) {
            logger.info("Creating new user for email: {}", email);
            user = new User();
            user.setEmail(email);
            user.setName(oauth2User.getAttribute("name"));
            user.setGoogleId(googleId);
            user.setPictureUrl(oauth2User.getAttribute("picture"));

             user.setRole(UserRole.USER);

            user = userService.saveUser(user);
            logger.info("New user created with ID: {}", user.getId());
        }

        Collection<GrantedAuthority> authorities = new ArrayList<>(oauth2User.getAuthorities());

        // Добавляем роль из нашей базы данных
        if (user.getRole() != null) {
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