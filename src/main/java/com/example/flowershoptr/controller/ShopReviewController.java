package com.example.flowershoptr.controller;

import com.example.flowershoptr.dto.ShopReview.ShopReviewCreateDTO;
import com.example.flowershoptr.dto.ShopReview.ShopReviewListDTO;
import com.example.flowershoptr.model.ShopReview;
import com.example.flowershoptr.service.ShopReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
public class ShopReviewController {
    private final ShopReviewService shopReviewService;


    @GetMapping
    public String review(Model model,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size) {
        Page<ShopReviewListDTO> listDTO = shopReviewService.Pagereview(page, size);
        model.addAttribute("listDTO", listDTO);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", listDTO.getTotalPages());
        return "client/review/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("shopReview", new ShopReviewCreateDTO());

        return "client/review/create";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute @Valid ShopReviewCreateDTO shopReviewCreateDTO,
                      BindingResult bindingResult,
                      @RequestParam(required = false) MultipartFile file,
                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            System.out.println("Ошибки валидации: " + bindingResult.getAllErrors());
            // Сохраняем ошибки для отображения на странице
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.shopReview", bindingResult);
            redirectAttributes.addFlashAttribute("shopReview", shopReviewCreateDTO);
            return "redirect:/review/create?error=validation";
        }

        shopReviewService.createShopReview(shopReviewCreateDTO, file);
        return "redirect:/review";
    }
}
