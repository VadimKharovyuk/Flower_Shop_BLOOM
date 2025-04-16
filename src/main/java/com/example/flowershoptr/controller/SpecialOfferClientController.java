package com.example.flowershoptr.controller;

import com.example.flowershoptr.dto.SpecialOffer.SpecialOfferDetailsDTO;
import com.example.flowershoptr.dto.SpecialOfferListDTO;
import com.example.flowershoptr.service.SpecialOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Контроллер для отображения специальных предложений на клиентской части сайта
 */
@Controller
@RequestMapping("/special-offers")
@RequiredArgsConstructor
public class SpecialOfferClientController {

    private final SpecialOfferService specialOfferService;

    /**
     * Отображает страницу со всеми активными специальными предложениями
     */
    @GetMapping
    public String showSpecialOffers(Model model) {
        List<SpecialOfferListDTO> activeOffers = specialOfferService.getAllActiveOffers();
        model.addAttribute("offers", activeOffers);
        return "client/special-offers/list";
    }

    /**
     * Отображает детальную информацию о конкретном специальном предложении
     */
    @GetMapping("/{id}")
    public String showOfferDetails(@PathVariable Long id, Model model) {
        SpecialOfferDetailsDTO offer = specialOfferService.getOfferDetailsDto(id);
        model.addAttribute("offer", offer);
        return "client/special-offers/details";
    }
}