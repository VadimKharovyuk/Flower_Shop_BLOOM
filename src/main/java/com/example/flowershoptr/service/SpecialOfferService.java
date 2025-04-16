package com.example.flowershoptr.service;

import com.example.flowershoptr.dto.SpecialOffer.SpecialOfferCreateDTO;
import com.example.flowershoptr.dto.SpecialOffer.SpecialOfferDetailsDTO;


import com.example.flowershoptr.dto.SpecialOfferListDTO;
import com.example.flowershoptr.model.Flower;
import com.example.flowershoptr.model.SpecialOffer;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


public interface SpecialOfferService {



    /**
     * Создает новое специальное предложение
     */
    SpecialOfferDetailsDTO createOffer(SpecialOfferCreateDTO specialOfferCreateDTO, MultipartFile imageFile);

    /**
     * Получает все активные специальные предложения
     */
    List<SpecialOfferListDTO> getAllActiveOffers();

    /**
     * Получает подробную информацию о специальном предложении
     */
    SpecialOfferDetailsDTO getOfferDetailsDto(Long offerId);

    /**
     * Добавляет цветы в специальное предложение
     */
    void addFlowersToOffer(Long offerId, List<Long> flowerIds);

    /**
     * Удаляет цветы из специального предложения
     */
    void removeFlowersFromOffer(Long offerId, List<Long> flowerIds);

    /**
     * Деактивирует все просроченные предложения
     */
    void deactivateExpiredOffers();

    /**
     * Получает цену со скидкой для указанного цветка
     */
    BigDecimal getDiscountedPrice(Flower flower);

    /**
     * Проверяет, есть ли активная скидка на цветок
     */
    boolean hasActiveDiscount(Flower flower);

    /**
     * Получает наилучшее предложение для цветка
     */
    Optional<SpecialOffer> getBestOffer(Flower flower);

    /**
     * Проверяет, действует ли еще акция
     */
    boolean isOfferStillValid(SpecialOffer offer);

    /**
     * Получает оставшееся время в днях до окончания акции
     */
    long getRemainingDays(SpecialOffer offer);

    /**
     * Вычисляет процент скидки
     */
    int getDiscountPercentage(SpecialOffer offer);
}