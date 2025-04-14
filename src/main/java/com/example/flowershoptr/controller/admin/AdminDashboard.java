package com.example.flowershoptr.controller.admin;

import com.example.flowershoptr.dto.category.CategoryListDTO;
import com.example.flowershoptr.model.Category;
import com.example.flowershoptr.model.Flower;
import com.example.flowershoptr.service.CategoryService;
import com.example.flowershoptr.service.FlowerService;
import com.example.flowershoptr.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminDashboard {

    private final FlowerService flowerService;
    private final CategoryService categoryService;
    private final PaginationUtils paginationUtils;

    @GetMapping
    public String adminDashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending,
            Model model) {

        long flowerCount = flowerService.flowerCount();
        model.addAttribute("flowerCount", flowerCount);

        Pageable pageable = paginationUtils.createPageable(page, size, sortBy, ascending);
        Page<CategoryListDTO> categories = categoryService.getAllCategories(pageable);
        model.addAttribute("categories", categories);

        return "admin/dashboard";
    }


}
