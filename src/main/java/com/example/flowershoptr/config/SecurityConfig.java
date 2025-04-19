package com.example.flowershoptr.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // Открытые URL, доступные всем
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
                                "/aut/login",     // Доступ к странице логина
                                "/aut/dashboard"
                        ).permitAll()
                        // Все остальные URL требуют авторизации
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/aut/login")  // Страница для OAuth2 логина
                        .defaultSuccessUrl("/aut/dashboard", true)
                )
                .formLogin(form -> form
                        .loginPage("/aut/login")  // Страница для формы логина
                        .defaultSuccessUrl("/aut/dashboard")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/aut/login"))  // Перенаправление при отсутствии авторизации
                );

        return http.build();
    }


}
