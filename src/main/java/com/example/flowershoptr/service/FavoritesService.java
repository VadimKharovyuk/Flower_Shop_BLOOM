package com.example.flowershoptr.service;

import com.example.flowershoptr.model.Flower;
import com.example.flowershoptr.repository.FlowerRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для работы с избранным через сессию
 */
@Service
public class FavoritesService {

    private static final String SESSION_FAVORITES_KEY = "userFavorites";

    private final FlowerRepository flowerRepository;

    @Autowired
    public FavoritesService(FlowerRepository flowerRepository) {
        this.flowerRepository = flowerRepository;
    }

    /**
     * Получает список ID товаров из избранного
     */
    @SuppressWarnings("unchecked")
    private Set<Long> getFavoritesIds(HttpSession session) {
        Set<Long> favoritesIds = (Set<Long>) session.getAttribute(SESSION_FAVORITES_KEY);

        if (favoritesIds == null) {
            favoritesIds = new HashSet<>();
            session.setAttribute(SESSION_FAVORITES_KEY, favoritesIds);
        }

        return favoritesIds;
    }

    /**
     * Добавляет товар в избранное
     */
    public void addToFavorites(Long flowerId, HttpSession session) {
        Set<Long> favoritesIds = getFavoritesIds(session);
        favoritesIds.add(flowerId);
        session.setAttribute(SESSION_FAVORITES_KEY, favoritesIds);
    }

    /**
     * Удаляет товар из избранного
     */
    public void removeFromFavorites(Long flowerId, HttpSession session) {
        Set<Long> favoritesIds = getFavoritesIds(session);
        favoritesIds.remove(flowerId);
        session.setAttribute(SESSION_FAVORITES_KEY, favoritesIds);
    }

    /**
     * Проверяет, есть ли товар в избранном
     */
    public boolean isInFavorites(Long flowerId, HttpSession session) {
        Set<Long> favoritesIds = getFavoritesIds(session);
        return favoritesIds.contains(flowerId);
    }

    /**
     * Возвращает количество товаров в избранном
     */
    public int getFavoritesCount(HttpSession session) {
        Set<Long> favoritesIds = getFavoritesIds(session);
        return favoritesIds.size();
    }

    /**
     * Очищает избранное
     */
    public void clearFavorites(HttpSession session) {
        session.removeAttribute(SESSION_FAVORITES_KEY);
    }

    /**
     * Возвращает все товары из избранного
     */
    public List<Flower> getFavoritesItems(HttpSession session) {
        Set<Long> favoritesIds = getFavoritesIds(session);

        if (favoritesIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Получаем товары из базы данных по их ID
        return flowerRepository.findAllById(favoritesIds);
    }
}