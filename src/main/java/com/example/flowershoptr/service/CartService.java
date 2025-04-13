package com.example.flowershoptr.service;


import com.example.flowershoptr.dto.cart.CartDto;
import com.example.flowershoptr.model.Cart;
import com.example.flowershoptr.model.Order;

import jakarta.servlet.http.HttpSession;

/**
 * Сервис для работы с корзиной покупок
 */
public interface CartService {

    /**
     * Получить или создать корзину в текущей сессии
     * @param session HTTP сессия пользователя
     * @return Объект корзины
     */
    Cart getOrCreateCartFromSession(HttpSession session);

    /**
     * Получить DTO корзины из текущей сессии
     * @param session HTTP сессия пользователя
     * @return DTO объект корзины
     */
    CartDto getCartDto(HttpSession session);

    /**
     * Добавить цветок в корзину
     * @param session HTTP сессия пользователя
     * @param flowerId ID цветка
     * @param quantity Количество
     * @return Обновленное DTO корзины
     */
    CartDto addFlowerToCart(HttpSession session, Long flowerId, Integer quantity);

    /**
     * Обновить количество цветка в корзине
     * @param session HTTP сессия пользователя
     * @param flowerId ID цветка
     * @param quantity Новое количество
     * @return Обновленное DTO корзины
     */
    CartDto updateCartItemQuantity(HttpSession session, Long flowerId, Integer quantity);

    /**
     * Удалить цветок из корзины
     * @param session HTTP сессия пользователя
     * @param flowerId ID цветка
     * @return Обновленное DTO корзины
     */
    CartDto removeFlowerFromCart(HttpSession session, Long flowerId);

    /**
     * Очистить корзину
     * @param session HTTP сессия пользователя
     */
    void clearCart(HttpSession session);

    /**
     * Преобразовать корзину в заказ
     * @param session HTTP сессия пользователя
     * @param clientName Имя клиента
     * @param clientPhone Телефон клиента
     * @param clientEmail Email клиента
     * @param deliveryAddress Адрес доставки
     * @param paymentMethod Способ оплаты
     * @return Созданный заказ
     */
    Order createOrderFromCart(HttpSession session, String clientName, String clientPhone,
                              String clientEmail, String deliveryAddress, String paymentMethod);

    /**
     * Привязать анонимную корзину к пользователю после авторизации
     * @param session HTTP сессия пользователя
     * @param userId ID пользователя
     * @return Обновленное DTO корзины
     */
    CartDto assignCartToUser(HttpSession session, Long userId);
}