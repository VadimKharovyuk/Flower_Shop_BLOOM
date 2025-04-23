package com.example.flowershoptr.service;

import java.util.List;
import com.example.flowershoptr.model.User;
import com.example.flowershoptr.model.UserProductView;

/**
 * Интерфейс для сервиса управления просмотрами товаров пользователями.
 * Поддерживает хранение в сессии и в базе данных.
 */
public interface ProductViewService {

    /**
     * Получает список просмотренных товаров для текущей сессии.
     *
     * @param sessionId ID сессии пользователя
     * @return Список просмотренных товаров
     */
    List<UserProductView> getViewsForSession(String sessionId);

    /**
     * Добавляет просмотр товара в историю просмотров сессии.
     *
     * @param sessionId ID сессии пользователя
     * @param productId ID просмотренного товара
     * @return Обновленный список просмотренных товаров
     */
    List<UserProductView> addView(String sessionId, Long productId);

    /**
     * Получает список просмотренных товаров для авторизованного пользователя.
     *
     * @param userId ID пользователя
     * @return Список просмотренных товаров
     */
    List<UserProductView> getUserViews(Long userId);

    /**
     * Сохраняет просмотр товара в историю просмотров пользователя.
     *
     * @param userId ID пользователя
     * @param productId ID товара
     */
    void saveUserView(Long userId, Long productId);

    /**
     * Переносит просмотры из сессии в базу данных для авторизованного пользователя.
     *
     * @param sessionId ID сессии
     * @param userId ID пользователя
     */
    void transferSessionViewsToUser(String sessionId, Long userId);

    /**
     * Получает просмотры для текущего пользователя или сессии.
     * Если пользователь авторизован, возвращает просмотры из БД,
     * иначе возвращает просмотры из сессии.
     *
     * @param sessionId ID сессии
     * @param user Текущий пользователь (может быть null)
     * @return Список просмотренных товаров
     */
    List<UserProductView> getRecentViews(String sessionId, User user);

    /**
     * Очищает историю просмотров для сессии.
     *
     * @param sessionId ID сессии
     */
    void clearSessionViews(String sessionId);

    /**
     * Очищает историю просмотров для пользователя.
     *
     * @param userId ID пользователя
     */
    void clearUserViews(Long userId);

    void saveUserViewByEmail(String email, Long id);

    List<UserProductView> getUserViewsByEmail(String email);

    void transferSessionViewsToUserByEmail(String id, String email);

}