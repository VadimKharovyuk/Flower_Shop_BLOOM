package com.example.flowershoptr.controller;

import com.example.flowershoptr.dto.flower.FlowerDetailsDTO;
import com.example.flowershoptr.dto.flower.FlowerListDTO;
import com.example.flowershoptr.service.FlowerService;
import com.example.flowershoptr.util.PaginationUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("flowers")
public class FlowerClientController {

    private final FlowerService flowerService;
    private final PaginationUtils paginationUtils;

    /**
     * Просмотр детальной информации о цветке
     */
    @GetMapping("/{id}")
    public String viewFlower(@PathVariable Long id, Model model , HttpServletRequest request) {
        log.info("Запрос на просмотр цветка с ID: {}", id);

        FlowerDetailsDTO flower = flowerService.getFlowerById(id);

        // Проверка, активен ли цветок
        if (!flower.isActive()) {
            log.warn("Попытка доступа к неактивному цветку: {}", id);
            return "redirect:/flowers";
        }model.addAttribute("request", request);

        model.addAttribute("flower", flower);

        // Дополнительно можно добавить рекомендуемые/похожие цветы
        Pageable pageable = paginationUtils.createPageable(0, 4, "name", true);
        Page<FlowerListDTO> recommendedFlowers = flowerService.getFlowersByCategory(
                flower.getCategory().getId(), pageable);


        model.addAttribute("recommendedFlowers", recommendedFlowers);

        return "client/flowers/view";
    }

    /**
     * Отображение всех цветов с пагинацией
     */
    @GetMapping
    public String viewAllFlowers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(
                ascending ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy
        ));

        Page<FlowerListDTO> flowers = flowerService.getActiveFlowers(pageable);

        model.addAttribute("flowers", flowers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", flowers.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("ascending", ascending);

        return "client/flowers/list";
    }


    /**
     * Отображение цветов с доставкой сегодня
     */
    @GetMapping("/delivery-today")
    public String viewFlowersWithDeliveryToday(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        log.info("Запрос на просмотр цветов с доставкой сегодня: page={}, size={}", page, size);

        Pageable pageable = paginationUtils.createPageable(page, size, "name", true);
        Page<FlowerListDTO> flowers = flowerService.getFlowersWithDeliveryToday(pageable);

        model.addAttribute("flowers", flowers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", flowers.getTotalPages());
        model.addAttribute("title", "Цветы с доставкой сегодня");

        return "client/flowers/delivery-today";
    }

    /**
     * Обработка добавления отзыва/рейтинга
     */
    @PostMapping("/{id}/rate")
    public String rateFlower(
            @PathVariable Long id,
            @RequestParam Double rating,
            @RequestParam(required = false) String reviewText) {

        log.info("Добавление рейтинга {} для цветка с ID: {}", rating, id);

        // Обновление рейтинга цветка
        flowerService.updateRating(id, rating);


        // Перенаправление обратно на страницу цветка
        return "redirect:/flowers/" + id;
    }

    /**
     * Поиск цветов по названию
     */
    @GetMapping("/search")
    public String searchFlowers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        Pageable pageable = paginationUtils.createPageable(page, size, "name", true);
        Page<FlowerListDTO> searchResults = flowerService.searchFlowersByName(query, pageable);

        model.addAttribute("flowers", searchResults);
        model.addAttribute("query", query);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", searchResults.getTotalPages());
        model.addAttribute("title", "Результаты поиска: " + query);

        return "client/flowers/search-results";
    }
}