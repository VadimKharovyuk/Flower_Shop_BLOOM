package com.example.flowershoptr.repository;

import com.example.flowershoptr.model.Flower;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlowerRepository extends JpaRepository<Flower, Long> {

    /**
     * Поиск активных цветов
     * @param pageable параметры пагинации и сортировки
     * @return страница активных цветов
     */
    Page<Flower> findByIsActiveTrue(Pageable pageable);

    /**
     * Поиск цветов по категории
     * @param categoryId ID категории
     * @param pageable параметры пагинации и сортировки
     * @return страница цветов в указанной категории
     */
    Page<Flower> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * Поиск цветов с доставкой сегодня
     * @param pageable параметры пагинации и сортировки
     * @return страница цветов с доставкой сегодня
     */
    Page<Flower> findByHasDeliveryTodayTrue(Pageable pageable);

    /**
     * Поиск активных цветов (без пагинации)
     * @return список активных цветов
     */
    List<Flower> findByIsActiveTrue();

    /**
     * Поиск цветов по категории (без пагинации)
     * @param categoryId ID категории
     * @return список цветов в указанной категории
     */
    List<Flower> findByCategoryId(Long categoryId);

    /**
     * Поиск цветов с доставкой сегодня (без пагинации)
     * @return список цветов с доставкой сегодня
     */
    List<Flower> findByHasDeliveryTodayTrue();

    /**
     * Поиск цветов с доставкой сегодня и активным статусом
     * @param pageable параметры пагинации и сортировки
     * @return страница активных цветов с доставкой сегодня
     */
    Page<Flower> findByIsActiveTrueAndHasDeliveryTodayTrue(Pageable pageable);

    /**
     * Поиск цветов по части имени (без учета регистра)
     * @param name часть имени
     * @param pageable параметры пагинации и сортировки
     * @return страница цветов, содержащих указанную строку в имени
     */
    Page<Flower> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Поиск цветов по категории и с доставкой сегодня
     * @param categoryId ID категории
     * @param pageable параметры пагинации и сортировки
     * @return страница цветов в указанной категории с доставкой сегодня
     */
    Page<Flower> findByCategoryIdAndHasDeliveryTodayTrue(Long categoryId, Pageable pageable);

    /**
     * Поиск цветов по наличию на складе (больше указанного количества)
     * @param minCount минимальное количество на складе
     * @param pageable параметры пагинации и сортировки
     * @return страница цветов, доступных в количестве не менее указанного
     */
    Page<Flower> findByCountGreaterThanEqual(Integer minCount, Pageable pageable);

    /**
     * Поиск цветов с доставкой сегодня и активным статусом (без пагинации)
     * @return список активных цветов с доставкой сегодня
     */
    List<Flower> findByIsActiveTrueAndHasDeliveryTodayTrue();

    /**
     * Поиск цветов по части имени (без учета регистра, без пагинации)
     * @param name часть имени
     * @return список цветов, содержащих указанную строку в имени
     */
    List<Flower> findByNameContainingIgnoreCase(String name);

    /**
     * Поиск цветов по категории и с доставкой сегодня (без пагинации)
     * @param categoryId ID категории
     * @return список цветов в указанной категории с доставкой сегодня
     */
    List<Flower> findByCategoryIdAndHasDeliveryTodayTrue(Long categoryId);

    /**
     * Поиск цветов по наличию на складе (больше указанного количества, без пагинации)
     * @param minCount минимальное количество на складе
     * @return список цветов, доступных в количестве не менее указанного
     */
    List<Flower> findByCountGreaterThanEqual(Integer minCount);

    Page<Flower> findAll(Specification<Flower> spec, Pageable pageable);

}