package com.example.flowershoptr.service.serviceImpl;

import com.example.flowershoptr.model.User;
import com.example.flowershoptr.model.UserProductView;
import com.example.flowershoptr.repository.UserProductViewRepository;
import com.example.flowershoptr.service.ProductViewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class ProductViewServiceImpl implements ProductViewService {

    @Value("${product.max-recent-views}")
    private int maxRecentViews;

    @Value("${product.max-user-views}")
    private int maxUserViews;

    @Autowired
    private CacheManager cacheManager;

    private static final String CACHE_NAME = "productViews";

    @Autowired
    private UserProductViewRepository userProductViewRepository;

    @Override
    @Cacheable(value = "productViews", key = "#sessionId")
    public List<UserProductView> getViewsForSession(String sessionId) {
        return new ArrayList<>();
    }


    @Override
    public List<UserProductView> addView(String sessionId, Long productId) {
        // Явно получаем кеш и проверяем его состояние
        Cache cache = cacheManager.getCache(CACHE_NAME);
        List<UserProductView> views;

        if (cache != null) {
            Cache.ValueWrapper wrapper = cache.get(sessionId);
            if (wrapper != null) {
                views = (List<UserProductView>) wrapper.get();
            } else {
                views = new ArrayList<>();
            }
        } else {
            views = new ArrayList<>();
        }

        // Логируем текущее состояние
        log.debug("До добавления нового просмотра, размер списка: {}", views.size());

        // Удаление предыдущего просмотра того же товара, если он был
        views.removeIf(entry -> entry.getProductId().equals(productId));

        // Добавление нового просмотра
        UserProductView newView = new UserProductView();
        newView.setProductId(productId);
        newView.setViewedAt(LocalDateTime.now());
        views.add(newView);

        // Ограничение размера списка
        if (views.size() > maxRecentViews) {
            views = new ArrayList<>(views.subList(views.size() - maxRecentViews, views.size()));
        }

        // Явно сохраняем обновленный список в кеш
        cache.put(sessionId, views);

        log.debug("После добавления нового просмотра, размер списка: {}", views.size());

        return views;
    }

    @Override
    public List<UserProductView> getUserViews(Long userId) {
        // Этот метод не используется, так как вы работаете с email
        return new ArrayList<>();
    }

    // Добавим метод для получения просмотров по email
    public List<UserProductView> getUserViewsByEmail(String email) {
        return userProductViewRepository.findByUserEmailOrderByViewedAtDesc(email);
    }

    @Override
    @Transactional
    public void saveUserView(Long userId, Long productId) {
        // Этот метод не используется, так как вы работаете с email
    }

    // Добавим метод для сохранения просмотров по email
    @Transactional
    public void saveUserViewByEmail(String email, Long productId) {
        // Удаление предыдущего просмотра того же товара (если он был)
        userProductViewRepository.deleteByUserEmailAndProductId(email, productId);

        // Создание новой записи о просмотре
        UserProductView userView = new UserProductView();
        userView.setUserEmail(email);
        userView.setProductId(productId);
        userView.setViewedAt(LocalDateTime.now());

        // Сохранение записи
        userProductViewRepository.save(userView);

        // Проверка на превышение лимита просмотров
        Long viewCount = userProductViewRepository.countByUserEmail(email);
        if (viewCount > maxUserViews) {
            List<UserProductView> allViews = userProductViewRepository.findByUserEmailOrderByViewedAtDesc(email);
            List<UserProductView> viewsToDelete = allViews.subList(maxUserViews, allViews.size());

            for (UserProductView viewToDelete : viewsToDelete) {
                userProductViewRepository.delete(viewToDelete);
            }
        }
    }

    @Override
    @Transactional
    public void transferSessionViewsToUser(String sessionId, Long userId) {
        // Этот метод не используется в том виде, как определен
    }

    // Добавим метод для переноса просмотров из сессии по email
    @Transactional
    public void transferSessionViewsToUserByEmail(String sessionId, String email) {
        List<UserProductView> sessionViews = getViewsForSession(sessionId);

        for (UserProductView view : sessionViews) {
            saveUserViewByEmail(email, view.getProductId());
        }

        // Очистка сессионных просмотров после переноса
        clearSessionViews(sessionId);
    }

    @Override
    public List<UserProductView> getRecentViews(String sessionId, User user) {
        if (user != null && user.getEmail() != null) {
            return getUserViewsByEmail(user.getEmail());
        } else {
            return getViewsForSession(sessionId);
        }
    }

    @Override
    @CacheEvict(value = "productViews", key = "#sessionId")
    public void clearSessionViews(String sessionId) {
        // Метод пустой, так как аннотация @CacheEvict уже выполняет очистку кеша
    }

    @Override
    @Transactional
    public void clearUserViews(Long userId) {
        // Этот метод не используется, так как вы работаете с email
    }

    // Добавим метод для очистки просмотров по email
    @Transactional
    public void clearUserViewsByEmail(String email) {
        userProductViewRepository.deleteByUserEmail(email);
    }
}