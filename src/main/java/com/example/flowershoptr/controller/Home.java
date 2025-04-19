package com.example.flowershoptr.controller;

import com.example.flowershoptr.dto.Event.EventListDto;
import com.example.flowershoptr.dto.ShopReview.ShopReviewListDTO;
import com.example.flowershoptr.dto.SpecialOfferListDTO;
import com.example.flowershoptr.dto.category.CategoryListDTO;
import com.example.flowershoptr.dto.flower.FlowerSearchDTO;
import com.example.flowershoptr.dto.flower.PopularFlowerDto;
import com.example.flowershoptr.model.Flower;
import com.example.flowershoptr.model.User;
import com.example.flowershoptr.service.*;
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
    private final CategoryService categoryService;
    private final EventService eventService;
    private final SpecialOfferService specialOfferService;
    private final ShopReviewService shopReviewService;
    private final InstagramService instagramService;
    private final UserService userService;



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

        List<CategoryListDTO> popularCategoryList = categoryService.getTotalCartAddCountByCategory(6);
        model.addAttribute("popularCategory", popularCategoryList);

        String s = session.getId();
        System.out.println("HttpSession создалась с номером " + s);



        List<EventListDto> listDtoPage = eventService.getFeaturedEvents(3);
        model.addAttribute("eventList", listDtoPage);



        List<SpecialOfferListDTO> activeOffers = specialOfferService.getAllActiveOffers();
        // Ограничиваем количество акций на главной странице (например, 3)
        if (activeOffers.size() > 3) {
            activeOffers = activeOffers.subList(0, 3);
        }
        model.addAttribute("offers", activeOffers);



        List<ShopReviewListDTO> shopReviewListDTOS = shopReviewService.listShopReviews(5);
        model.addAttribute("shopReviewList", shopReviewListDTOS);




        model.addAttribute("instagramPosts", instagramService.getActivePostsLimited(6));


        return "home";
    }







    @GetMapping("/test")
    public String test(Model model) {
    return "test";
    }
}
