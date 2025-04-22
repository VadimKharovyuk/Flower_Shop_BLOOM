package com.example.flowershoptr.controller;

import com.example.flowershoptr.dto.category.CategoryDetailsDTO;
import com.example.flowershoptr.dto.category.CategoryListDTO;
import com.example.flowershoptr.dto.flower.FlowerSearchDTO;
import com.example.flowershoptr.dto.flower.PopularFlowerDto;
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

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j

@RequestMapping("categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final FlowerService flowerService;
    private final PaginationUtils paginationUtils;

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
        model.addAttribute("currentUrl", "/categories/" + id);
        // Проверяем, активна ли категория
        if (!category.isActive()) {
            log.warn("Попытка доступа к неактивной категории: {}", id);
            return "redirect:/categories";
        }

        model.addAttribute("category", category);

        Pageable pageable = paginationUtils.createPageable(page, size, sortBy, ascending);
        model.addAttribute("flowers", flowerService.getFlowersByCategory(id, pageable));
        model.addAttribute("currentPage", page);

        List<PopularFlowerDto> popularFlowerDtos = flowerService.getPopularFlowersList();
        model.addAttribute("popularFlower", popularFlowerDtos);

        return "client/categories/view";
    }


    /**
     * Поиск товара по названию
     */
    @GetMapping("/search")
    public String searchFlowers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        Pageable pageable = paginationUtils.createPageable(page, size, "name", true);
        Page<FlowerSearchDTO> searchResults = flowerService.searchFlowersFindByName(query, pageable);

        model.addAttribute("flowers", searchResults);
        model.addAttribute("query", query);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", searchResults.getTotalPages());

        return "client/categories/search-results";
    }

    @GetMapping("/filter")
    public String filterFlowers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer rating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        Pageable pageable = paginationUtils.createPageable(page, size, "name", true);
        Page<FlowerSearchDTO> filterPriceResults = flowerService.searchFlowersWithFilters(query, minPrice, maxPrice, inStock,  rating ,pageable);

        model.addAttribute("flowers", filterPriceResults);

        model.addAttribute("query", query);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", filterPriceResults.getTotalPages());

        return "client/categories/search-results";
    }
}