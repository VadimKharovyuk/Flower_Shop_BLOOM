package com.example.flowershoptr.controller;

import com.example.flowershoptr.dto.category.CategoryDetailsDTO;
import com.example.flowershoptr.dto.category.CategoryListDTO;
import com.example.flowershoptr.service.CategoryService;

import com.example.flowershoptr.service.FlowerService;
import com.example.flowershoptr.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final FlowerService flowerService;
    private final PaginationUtils paginationUtils;

    /**
     * Отображение всех активных категорий на главной странице
     */
    @GetMapping()
    public String listCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending,
            Model model) {

        log.info("Запрос страницы категорий: page={}, size={}", page, size);

        // Получаем только активные категории
        Pageable pageable = paginationUtils.createPageable(page, size, sortBy, ascending);
        Page<CategoryListDTO> categories = categoryService.getActiveCategories(pageable);

        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categories.getTotalPages());

        return "client/categories/list";
    }

    /**
     * Отображение категорий на главной странице (только избранные категории)
     */
//    @GetMapping("/")
//    public String homePage(Model model) {
//        log.info("Запрос главной страницы");
//
//        // Получаем избранные категории для главной страницы
//        Pageable pageable = paginationUtils.createPageable(0, 6, "name", true);
//        Page<CategoryListDTO> featuredCategories = categoryService.getFeaturedCategories(pageable);
//
//        model.addAttribute("featuredCategories", featuredCategories.getContent());
//
//        // Здесь можно добавить популярные товары, акции и т.д.
//
//        return "client/home";
//    }

    /**
     * Просмотр конкретной категории с ее товарами
     */
    @GetMapping("/{id}")
    public String viewCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending,
            Model model) {

        log.info("Запрос категории {}: page={}, size={}", id, page, size);

        // Получаем детали категории
        CategoryDetailsDTO category = categoryService.getCategoryById(id);

        // Проверяем, активна ли категория
        if (!category.isActive()) {
            log.warn("Попытка доступа к неактивной категории: {}", id);
            return "redirect:/categories";
        }

        model.addAttribute("category", category);

        Pageable pageable = paginationUtils.createPageable(page, size, sortBy, ascending);
        model.addAttribute("flowers", flowerService.getFlowersByCategory(id, pageable));
        model.addAttribute("currentPage", page);

        return "client/categories/view";
    }

    /**
     * Поиск категорий по названию
     */
    @GetMapping("/categories/search")
    public String searchCategories(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        log.info("Поиск категорий по запросу '{}': page={}, size={}", query, page, size);

        // Предполагается, что у вас есть такой метод в CategoryService
        Pageable pageable = paginationUtils.createPageable(page, size, "name", true);
//        Page<CategoryListDTO> searchResults = categoryService.searchCategories(query, pageable);
//
//        model.addAttribute("categories", searchResults);
//        model.addAttribute("query", query);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", searchResults.getTotalPages());

        return "client/categories/search-results";
    }
}