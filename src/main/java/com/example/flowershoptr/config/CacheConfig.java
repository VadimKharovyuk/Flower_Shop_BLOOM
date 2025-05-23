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
                .expireAfterWrite(5, TimeUnit.DAYS)
                .initialCapacity(10)
                .maximumSize(50)
                .evictionListener((key, value, cause) ->
                        log.warn("CategoriesList cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("categoriesList", categoriesListCache.build());

        // Кеш для отдельных категорий по ID
        Caffeine<Object, Object> categoryByIdCache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.DAYS)
                .initialCapacity(50)
                .maximumSize(100)
                .evictionListener((key, value, cause) ->
                        log.warn("CategoryById cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("categoryById", categoryByIdCache.build());

        // Кеш для списка цветов
        Caffeine<Object, Object> flowersListCache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.DAYS)
                .initialCapacity(20)
                .maximumSize(50)
                .evictionListener((key, value, cause) ->
                        log.warn("FlowersList cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("flowersList", flowersListCache.build());

        // Кеш для отдельных цветов по ID
        Caffeine<Object, Object> flowerByIdCache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.DAYS)
                .initialCapacity(100)
                .maximumSize(500)
                .evictionListener((key, value, cause) ->
                        log.warn("FlowerById cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("flowerById", flowerByIdCache.build());

        // Кеш для страниц цветов
        Caffeine<Object, Object> flowersPageListCache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.DAYS)
                .initialCapacity(30)
                .maximumSize(100)
                .evictionListener((key, value, cause) ->
                        log.warn("FlowersPageList cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("flowersPageList", flowersPageListCache.build());

        // Кеш для страниц активных цветов
        Caffeine<Object, Object> activeFlowersPageListCache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)  // Меньшее время для активных цветов, т.к. они могут чаще меняться
                .initialCapacity(30)
                .maximumSize(100)
                .evictionListener((key, value, cause) ->
                        log.warn("ActiveFlowersPageList cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("activeFlowersPageList", activeFlowersPageListCache.build());

        // Кеш для страниц активных категорий
        Caffeine<Object, Object> activeCategoriesPageListCache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.DAYS)
                .initialCapacity(20)
                .maximumSize(50)
                .evictionListener((key, value, cause) ->
                        log.warn("ActiveCategoriesPageList cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("activeCategoriesPageList", activeCategoriesPageListCache.build());

        // Кеш для списка цветов по категории
        Caffeine<Object, Object> flowersByCategoryCache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.DAYS)
                .initialCapacity(50)
                .maximumSize(200)
                .evictionListener((key, value, cause) ->
                        log.warn("FlowersByCategory cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("flowersByCategory", flowersByCategoryCache.build());

        // НОВЫЕ КЕШИ ДЛЯ БЛОГ-ПОСТОВ

        // Кеш для страниц блог-постов
        Caffeine<Object, Object> blogPostsPageListCache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.DAYS)
                .initialCapacity(20)
                .maximumSize(100)
                .evictionListener((key, value, cause) ->
                        log.warn("BlogPostsPageList cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("blogPostsPageList", blogPostsPageListCache.build());

        // Кеш для отдельных блог-постов по ID
        Caffeine<Object, Object> blogPostByIdCache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.DAYS)
                .initialCapacity(50)
                .maximumSize(200)
                .evictionListener((key, value, cause) ->
                        log.warn("BlogPostById cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("blogPostById", blogPostByIdCache.build());

        // Кеш для блог-постов по типу
        Caffeine<Object, Object> blogPostsByTypeCache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.DAYS)
                .initialCapacity(30)
                .maximumSize(150)
                .evictionListener((key, value, cause) ->
                        log.warn("BlogPostsByType cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("blogPostsByType", blogPostsByTypeCache.build());

        // Кеш для популярных блог-постов
        Caffeine<Object, Object> popularBlogPostsCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.DAYS) // Меньшее время для популярных постов, т.к. они могут чаще просматриваться
                .initialCapacity(20)
                .maximumSize(100)
                .evictionListener((key, value, cause) ->
                        log.warn("PopularBlogPostsPageList cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("popularBlogPostsPageList", popularBlogPostsCache.build());

        // НОВЫЕ КЕШИ ДЛЯ СОБЫТИЙ

        // Кеш для страниц активных событий
        Caffeine<Object, Object> activeEventsPageListCache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.DAYS)
                .initialCapacity(20)
                .maximumSize(100)
                .evictionListener((key, value, cause) ->
                        log.warn("ActiveEventsPageList cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("activeEventsPageList", activeEventsPageListCache.build());

        // Кеш для избранных событий
        Caffeine<Object, Object> featuredEventsListCache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.DAYS)
                .initialCapacity(10)
                .maximumSize(50)
                .evictionListener((key, value, cause) ->
                        log.warn("FeaturedEventsList cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("featuredEventsList", featuredEventsListCache.build());

        // Кеш для отдельных событий по ID
        Caffeine<Object, Object> eventByIdCache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.DAYS)
                .initialCapacity(30)
                .maximumSize(150)
                .evictionListener((key, value, cause) ->
                        log.warn("EventById cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("eventById", eventByIdCache.build());

        // Кеш для списка популярных цветов
        Caffeine<Object, Object> popularFlowersListCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.DAYS)
                .initialCapacity(10)
                .maximumSize(30)
                .evictionListener((key, value, cause) ->
                        log.warn("PopularFlowersList cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("popularFlowersList", popularFlowersListCache.build());

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