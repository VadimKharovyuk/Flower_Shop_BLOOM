package com.example.flowershoptr.controller.admin;

import com.example.flowershoptr.dto.SpecialOffer.FlowerShortDTO;
import com.example.flowershoptr.dto.SpecialOffer.SpecialOfferDetailsDTO;
import com.example.flowershoptr.dto.SpecialOfferCreateDTO;
import com.example.flowershoptr.dto.SpecialOfferListDTO;
import com.example.flowershoptr.model.Flower;
import com.example.flowershoptr.service.FlowerService;
import com.example.flowershoptr.service.SpecialOfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/special-offers")
@RequiredArgsConstructor
public class AdminSpecialOfferMvcController {

    private final SpecialOfferService specialOfferService;
    private final FlowerService flowerService;

    /**
     * Страница со списком всех специальных предложений
     */
    @GetMapping
    public String listOffers(Model model) {
        List<SpecialOfferListDTO> offers = specialOfferService.getAllActiveOffers();
        model.addAttribute("offers", offers);
        model.addAttribute("currentPage", "special-offers");
        return "admin/special-offers/list";
    }

    /**
     * Страница с детальной информацией о специальном предложении
     */
    @GetMapping("/{id}")
    public String viewOffer(@PathVariable Long id, Model model) {
        SpecialOfferDetailsDTO offer = specialOfferService.getOfferDetailsDto(id);
        model.addAttribute("offer", offer);
        model.addAttribute("currentPage", "special-offers");
        return "admin/special-offers/view";
    }

    /**
     * Страница с формой создания нового специального предложения
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("offerDto", new SpecialOfferCreateDTO());

        // Получаем список всех доступных цветов для выбора
        List<Flower> availableFlowers = flowerService.getAllActiveFlowers();
        model.addAttribute("availableFlowers", availableFlowers);
        model.addAttribute("currentPage", "special-offers");

        return "admin/special-offers/create";
    }

    /**
     * Обработка отправки формы создания специального предложения
     */
    @PostMapping("/create")
    public String createOffer(
            @Valid @ModelAttribute("offerDto") SpecialOfferCreateDTO offerDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/special-offers/create";
        }

        // Создание предложения
        SpecialOfferDetailsDTO createdOffer = specialOfferService.createOffer(offerDto);
        redirectAttributes.addFlashAttribute("successMessage",
                "Специальное предложение успешно создано");

        return "redirect:/admin/special-offers/" + createdOffer.getId();
    }

    /**
     * Страница с формой редактирования специального предложения
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        SpecialOfferDetailsDTO offerDetails = specialOfferService.getOfferDetailsDto(id);

        // Преобразуем DTO в форму для редактирования
        SpecialOfferCreateDTO editDto = new SpecialOfferCreateDTO();
        // Здесь нужно заполнить editDto данными из offerDetails
        // (можно реализовать через маппер или вручную)

        model.addAttribute("offerDto", editDto);
        model.addAttribute("offerId", id);

        // Получаем список всех доступных цветов для выбора
        List<Flower> availableFlowers = flowerService.getAllActiveFlowers();
        model.addAttribute("availableFlowers", availableFlowers);

        // Получаем список ID цветов, уже добавленных в предложение
        List<Long> selectedFlowerIds = offerDetails.getApplicableFlowers().stream()
                .map(FlowerShortDTO::getId)
                .toList();
        model.addAttribute("selectedFlowerIds", selectedFlowerIds);
        model.addAttribute("currentPage", "special-offers");

        return "admin/special-offers/edit";
    }

    /**
     * Обработка отправки формы редактирования специального предложения
     */
    @PostMapping("/{id}/edit")
    public String updateOffer(
            @PathVariable Long id,
            @Valid @ModelAttribute("offerDto") SpecialOfferCreateDTO offerDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/special-offers/edit";
        }

        // Обновление предложения (нужно добавить метод в сервис)
//         SpecialOfferDetailsDTO updatedOffer = specialOfferService.updateOffer(id, offerDto);

        redirectAttributes.addFlashAttribute("successMessage",
                "Специальное предложение успешно обновлено");

        return "redirect:/admin/special-offers/" + id;
    }

    /**
     * Обработка запроса на деактивацию специального предложения
     */
    @PostMapping("/{id}/deactivate")
    public String deactivateOffer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        // Деактивация предложения (нужно добавить метод в сервис)
        // specialOfferService.deactivateOffer(id);

        redirectAttributes.addFlashAttribute("successMessage",
                "Специальное предложение успешно деактивировано");

        return "redirect:/admin/special-offers";
    }

    /**
     * Страница управления цветами в специальном предложении
     */
    @GetMapping("/{id}/manage-flowers")
    public String showManageFlowersForm(@PathVariable Long id, Model model) {
        SpecialOfferDetailsDTO offer = specialOfferService.getOfferDetailsDto(id);
        model.addAttribute("offer", offer);

        // Получаем список всех доступных цветов
        List<Flower> allFlowers = flowerService.getAllActiveFlowers();
        model.addAttribute("availableFlowers", allFlowers);

        // Получаем список ID цветов, уже добавленных в предложение
        List<Long> selectedFlowerIds = offer.getApplicableFlowers().stream()
                .map(flower -> flower.getId())
                .toList();
        model.addAttribute("selectedFlowerIds", selectedFlowerIds);
        model.addAttribute("currentPage", "special-offers");

        return "admin/special-offers/manage-flowers";
    }

    /**
     * Обработка запроса на обновление списка цветов в специальном предложении
     */
    @PostMapping("/{id}/manage-flowers")
    public String updateFlowersInOffer(
            @PathVariable Long id,
            @RequestParam("flowerIds") List<Long> selectedFlowerIds,
            RedirectAttributes redirectAttributes) {

        // Получаем текущие цветы в предложении
        SpecialOfferDetailsDTO offer = specialOfferService.getOfferDetailsDto(id);
        List<Long> currentFlowerIds = offer.getApplicableFlowers().stream()
                .map(flower -> flower.getId())
                .toList();

        // Удаляем цветы, которые были убраны из выбора
        currentFlowerIds.stream()
                .filter(flowerId -> !selectedFlowerIds.contains(flowerId))
                .toList()
                .forEach(flowerId -> {
                    specialOfferService.removeFlowersFromOffer(id, List.of(flowerId));
                });

        // Добавляем новые выбранные цветы
        selectedFlowerIds.stream()
                .filter(flowerId -> !currentFlowerIds.contains(flowerId))
                .toList()
                .forEach(flowerId -> {
                    specialOfferService.addFlowersToOffer(id, List.of(flowerId));
                });

        redirectAttributes.addFlashAttribute("successMessage",
                "Список цветов в специальном предложении успешно обновлен");

        return "redirect:/admin/special-offers/" + id;
    }

    /**
     * Запуск процесса деактивации просроченных предложений
     */
    @PostMapping("/deactivate-expired")
    public String deactivateExpiredOffers(RedirectAttributes redirectAttributes) {
        specialOfferService.deactivateExpiredOffers();

        redirectAttributes.addFlashAttribute("successMessage",
                "Просроченные специальные предложения успешно деактивированы");

        return "redirect:/admin/special-offers";
    }
}