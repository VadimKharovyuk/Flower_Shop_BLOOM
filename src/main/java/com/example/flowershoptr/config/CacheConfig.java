package com.example.flowershoptr.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // Кеш для списка категорий
        Caffeine<Object, Object> categoriesListCache = Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .initialCapacity(10)
                .maximumSize(50)
                .evictionListener((key, value, cause) ->
                        log.warn("CategoriesList cache eviction: key={}, cause={}", key, cause))
                .recordStats();

        // Кеш для отдельных категорий по ID
        Caffeine<Object, Object> categoryByIdCache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .initialCapacity(50)
                .maximumSize(100)
                .evictionListener((key, value, cause) ->
                        log.warn("CategoryById cache eviction: key={}, cause={}", key, cause))
                .recordStats();

        // Кеш для списка цветов
        Caffeine<Object, Object> flowersListCache = Caffeine.newBuilder()
                .expireAfterWrite(20, TimeUnit.MINUTES)
                .initialCapacity(20)
                .maximumSize(50)
                .evictionListener((key, value, cause) ->
                        log.warn("FlowersList cache eviction: key={}, cause={}", key, cause))
                .recordStats();

        // Кеш для отдельных цветов по ID
        Caffeine<Object, Object> flowerByIdCache = Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .initialCapacity(100)
                .maximumSize(500)
                .evictionListener((key, value, cause) ->
                        log.warn("FlowerById cache eviction: key={}, cause={}", key, cause))
                .recordStats();



        Caffeine<Object, Object> flowersPageListCache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .initialCapacity(30)
                .maximumSize(100)
                .evictionListener((key, value, cause) ->
                        log.warn("FlowersPageList cache eviction: key={}, cause={}", key, cause))
                .recordStats();

// Кеш для страниц активных цветов
        Caffeine<Object, Object> activeFlowersPageListCache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)  // Меньшее время для активных цветов, т.к. они могут чаще меняться
                .initialCapacity(30)
                .maximumSize(100)
                .evictionListener((key, value, cause) ->
                        log.warn("ActiveFlowersPageList cache eviction: key={}, cause={}", key, cause))
                .recordStats();



        // Кеш для страниц активных категорий
        Caffeine<Object, Object> activeCategoriesPageListCache = Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .initialCapacity(20)
                .maximumSize(50)
                .evictionListener((key, value, cause) ->
                        log.warn("ActiveCategoriesPageList cache eviction: key={}, cause={}", key, cause))
                .recordStats();

// Регистрируем кеш
        cacheManager.registerCustomCache("activeCategoriesPageList", activeCategoriesPageListCache.build());



        // Кеш для списка цветов по категории
        Caffeine<Object, Object> flowersByCategoryCache = Caffeine.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .initialCapacity(50)
                .maximumSize(200)
                .evictionListener((key, value, cause) ->
                        log.warn("FlowersByCategory cache eviction: key={}, cause={}", key, cause))
                .recordStats();

// Регистрируем кеш
        cacheManager.registerCustomCache("flowersByCategory", flowersByCategoryCache.build());



        // Регистрируем кеши отдельно с более понятными именами
        cacheManager.registerCustomCache("categoriesList", categoriesListCache.build());
        cacheManager.registerCustomCache("categoryById", categoryByIdCache.build());
        cacheManager.registerCustomCache("flowersList", flowersListCache.build());





        // Для пагинированных списков   client
        cacheManager.registerCustomCache("flowerById", flowerByIdCache.build());

        // Для пагинированных списков  admin
        cacheManager.registerCustomCache("flowersPageList", activeFlowersPageListCache.build());


        // Для пагинированных списков  client
        cacheManager.registerCustomCache("activeFlowersPageList", flowersPageListCache.build());



        return cacheManager;
    }


    // Обработчик ошибок кеша
    @Bean
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.error("Cache GET error: cache={}, key={}", cache.getName(), key, e);
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                log.error("Cache PUT error: cache={}, key={}", cache.getName(), key, e);
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.error("Cache EVICT error: cache={}, key={}", cache.getName(), key, e);
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.error("Cache CLEAR error: cache={}", cache.getName(), e);
            }
        };
    }
}