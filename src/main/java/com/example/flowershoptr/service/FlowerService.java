package com.example.flowershoptr.service;

import com.example.flowershoptr.dto.category.CategoryListDTO;
import com.example.flowershoptr.dto.flower.*;
import com.example.flowershoptr.model.Flower;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface FlowerService {
    /**
     * Получить страницу списка всех цветов
     * @param pageable параметры пагинации и сортировки
     * @return страница цветов в формате списка
     */
    Page<FlowerListDTO> getAllFlowers(Pageable pageable);

    List<PopularFlowerDto> getFavoritesFlowersList(int limit);

    /**
     * Получить страницу списка активных цветов
     * @param pageable параметры пагинации и сортировки
     * @return страница активных цветов
     */
    Page<FlowerListDTO> getActiveFlowers(Pageable pageable);

    /**
     * Получить страницу списка цветов по категории
     * @param categoryId ID категории
     * @param pageable параметры пагинации и сортировки
     * @return страница цветов в указанной категории
     */
    Page<FlowerListDTO> getFlowersByCategory(Long categoryId, Pageable pageable);

    /**
     * Получить детальную информацию о цветке
     * @param id ID цветка
     * @return детальная информация о цветке
     */
    FlowerDetailsDTO getFlowerById(Long id);

    /**
     * Создать новый цветок
     * @param createFlowerDTO DTO для создания цветка
     * @param categoryId ID категории
     * @return созданный цветок в детальном формате
     */
    FlowerDetailsDTO createFlower(CreateFlowerDTO createFlowerDTO, Long categoryId);

    /**
     * Создать новый цветок с изображением
     * @param createFlowerDTO DTO для создания цветка
     * @param categoryId ID категории
     * @param imageFile файл изображения
     * @return созданный цветок в детальном формате
     */
    FlowerDetailsDTO createFlowerWithImage(CreateFlowerDTO createFlowerDTO, Long categoryId, MultipartFile imageFile);

    /**
     * Обновить существующий цветок
     * @param id ID цветка для обновления
     * @param updateFlowerDTO DTO с обновленными данными
     * @param categoryId ID категории (может быть null если категория не меняется)
     * @return обновленный цветок в детальном формате
     */
    FlowerDetailsDTO updateFlower(Long id, UpdateFlowerDTO updateFlowerDTO, Long categoryId);

    /**
     * Обновить существующий цветок с изображением
     * @param id ID цветка для обновления
     * @param updateFlowerDTO DTO с обновленными данными
     * @param categoryId ID категории (может быть null если категория не меняется)
     * @param imageFile файл изображения
     * @return обновленный цветок в детальном формате
     */
    FlowerDetailsDTO updateFlowerWithImage(Long id, UpdateFlowerDTO updateFlowerDTO, Long categoryId, MultipartFile imageFile);

    /**
     * Удалить цветок
     * @param id ID цветка для удаления
     * @return true если удаление успешно, иначе false
     */
    boolean deleteFlower(Long id);

    /**
     * Обновить рейтинг цветка
     * @param id ID цветка
     * @param rating новый рейтинг
     * @return обновленный цветок в детальном формате
     */
    FlowerDetailsDTO updateRating(Long id, Double rating);

    /**
     * Получить страницу цветов с доставкой сегодня
     * @param pageable параметры пагинации и сортировки
     * @return страница цветов с доставкой сегодня
     */
    Page<FlowerListDTO> getFlowersWithDeliveryToday(Pageable pageable);

    /**
     * Обновить количество товара на складе
     * @param id ID цветка
     * @param newCount новое количество
     * @return обновленный цветок в детальном формате
     */
    FlowerDetailsDTO updateStock(Long id, Integer newCount);

    /**
     * Поиск цветов по имени
     * @param name часть имени для поиска
     * @param pageable параметры пагинации и сортировки
     * @return страница цветов, соответствующих поисковому запросу
     */
    Page<FlowerListDTO> searchFlowersByName(String name, Pageable pageable);

    /**
     * Получить список всех цветов (без пагинации)
     * Используется для экспорта и других операций, требующих полного набора данных
     * @return полный список цветов
     */
    List<FlowerListDTO> getAllFlowersWithoutPagination();

    long flowerCount();

    Page<FlowerSearchDTO> searchFlowersFindByName(String query, Pageable pageable);


    Page<FlowerSearchDTO> searchFlowersWithFilters(String query, BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock, Pageable pageable);

    Page<PopularFlowerDto> getPopularFlowers(Pageable pageable);
    Page<PopularFlowerDto> getFavoritesFlowers(Pageable pageable);



    List<PopularFlowerDto> getFavoritesFlowersList();
    List<PopularFlowerDto> getPopularFlowersList();


    List<Flower> getAllActiveFlowers();
}