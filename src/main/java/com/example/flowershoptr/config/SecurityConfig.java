package com.example.flowershoptr.config;



import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/admin/**").access((authentication, context) -> {
                            Authentication auth = authentication.get();
                            Object principal = auth.getPrincipal();

                            if (principal instanceof OAuth2User) {
                                OAuth2User oauth2User = (OAuth2User) principal;
                                String email = oauth2User.getAttribute("email");
                                return new AuthorizationDecision("vadimkh17@gmail.com".equals(email));
                            } else {
                                // Для других типов аутентификации
                                String username = auth.getName();
                                // Логика для проверки других типов аутентификации, если нужно
                                return new AuthorizationDecision(false);
                            }
                        })

                        // Затем открытые URL, доступные всем
                        .requestMatchers(
                                "/",
                                "/trending/**",
                                "/special-offers/**",
                                "/review/**",
                                "/profile/**",
                                "/cart",
                                "/events/**",
                                "/flowers/**",
                                "/login",
                                "/order-tracking/**",
                                "/register",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/products/**",
                                "/categories/**",
                                "/contact",
                                "/about",
                                "/blog/**",
                                "/api/**",
                                "/error",
                                "/favicon.ico",
                                "/aut/login",
                                "/aut/dashboard",
                                "/fix-admin"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/aut/login")
                        .defaultSuccessUrl("/aut/dashboard", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                )
                .formLogin(form -> form
                        .loginPage("/aut/login")
                        .defaultSuccessUrl("/aut/dashboard")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                )

                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/aut/login"))
                );


        return http.build();
    }


}
