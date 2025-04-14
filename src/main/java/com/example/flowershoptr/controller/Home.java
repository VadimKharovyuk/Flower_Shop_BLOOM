package com.example.flowershoptr.controller;

import com.example.flowershoptr.dto.flower.FlowerSearchDTO;
import com.example.flowershoptr.dto.flower.PopularFlowerDto;
import com.example.flowershoptr.model.Flower;
import com.example.flowershoptr.service.FlowerService;
import com.example.flowershoptr.util.PaginationUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class Home {
    private final FlowerService flowerService;



    @GetMapping
    public String homePage(HttpSession session, Model model){



        List<PopularFlowerDto> popularFlowers = flowerService.getPopularFlowersList().stream()
                .limit(6)
                .collect(Collectors.toList());

        model.addAttribute("popularFlowers", popularFlowers);

        List<PopularFlowerDto> favoriteFlowers = flowerService.getFavoritesFlowersList().stream()
                .limit(6)
                .collect(Collectors.toList());


        model.addAttribute("favoriteFlowers", favoriteFlowers);



        String s = session.getId();
        System.out.println("HttpSession создалась с номером " + s);

        return "home";
    }


    @GetMapping("/trending")
    public String showTrendingFlowers( Model model,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "8") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<PopularFlowerDto> popularFlowers = flowerService.getPopularFlowers(pageable);
        Page<PopularFlowerDto> favoriteFlowers = flowerService.getFavoritesFlowers(pageable);

        model.addAttribute("popularFlowers", popularFlowers);
        model.addAttribute("favoriteFlowers", favoriteFlowers);

        return "/trending/trending-flowers";
    }




    @GetMapping("/test")
    public String test(Model model) {
    return "test";
    }
}
