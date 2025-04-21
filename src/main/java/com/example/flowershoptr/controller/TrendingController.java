package com.example.flowershoptr.controller;

import com.example.flowershoptr.dto.category.CategoryListDTO;
import com.example.flowershoptr.dto.flower.PopularFlowerDto;
import com.example.flowershoptr.service.CategoryService;
import com.example.flowershoptr.service.FlowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trending")
public class TrendingController {
    private final FlowerService flowerService;
    private final CategoryService categoryService;



    @GetMapping()
    public String showTrendingFlowers( Model model,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "8") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<PopularFlowerDto> popularFlowers = flowerService.getPopularFlowers(pageable);
        Page<PopularFlowerDto> favoriteFlowers = flowerService.getFavoritesFlowers(pageable);

        model.addAttribute("popularFlowers", popularFlowers);
        model.addAttribute("favoriteFlowers", favoriteFlowers);


        List<CategoryListDTO> popularCategoryList = categoryService.getTotalCartAddCountByCategory(5);
        model.addAttribute("popularCategory", popularCategoryList);
        return "trending/trending-flowers";
    }
}

