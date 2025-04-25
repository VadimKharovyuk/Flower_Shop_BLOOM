package com.example.flowershoptr.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@RequiredArgsConstructor
//  для фильтрации запросов на Render API
@Component
public class ApiSecretFilter extends OncePerRequestFilter {

    @Value("${api.secret}")
    private String apiSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Отладочный вывод
        System.out.println("Request path: " + path);
        System.out.println("Expected API Secret: " + apiSecret);
        System.out.println("Received API Secret: " + request.getHeader("X-API-Secret"));

        // Проверяем только API Render эндпоинты
        if (path.startsWith("/api/render")) {
            String requestApiSecret = request.getHeader("X-API-Secret");

            if (requestApiSecret == null || !requestApiSecret.equals(apiSecret)) {
                System.out.println("Unauthorized access attempt");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{\"error\":\"Unauthorized\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
