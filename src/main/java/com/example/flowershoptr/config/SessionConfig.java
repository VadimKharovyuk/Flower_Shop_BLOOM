package com.example.flowershoptr.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.filter.DelegatingFilterProxy;

import jakarta.servlet.DispatcherType;

import java.util.EnumSet;

@Configuration
@EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 86400) // 1 день
public class SessionConfig extends AbstractHttpSessionApplicationInitializer {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("FLOWERSHOP_SESSION");
        serializer.setCookiePath("/");
        serializer.setCookieMaxAge(86400); // 1 день в секундах
        serializer.setUseSecureCookie(true);  // Для HTTPS
        serializer.setUseHttpOnlyCookie(true);  // Защита от XSS
        return serializer;
    }

    @Bean

    public FilterRegistrationBean<DelegatingFilterProxy> customSpringSessionRepositoryFilter() {
        FilterRegistrationBean<DelegatingFilterProxy> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new DelegatingFilterProxy("springSessionRepositoryFilter"));
        filterRegistrationBean.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST));

        // Применять фильтр только для определенных URL
        filterRegistrationBean.addUrlPatterns("/cart/*");
        filterRegistrationBean.addUrlPatterns("/order/*");
        filterRegistrationBean.addUrlPatterns("/checkout/*");

        // Не применять фильтр для статических ресурсов и API запросов, где не нужна сессия
        filterRegistrationBean.addInitParameter("excludedPaths", "/images/*, /css/*, /js/*, /api/catalog/*, /api/categories/*");

        return filterRegistrationBean;
    }
}