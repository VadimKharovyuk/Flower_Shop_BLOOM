package com.example.flowershoptr.controller.admin;

import com.example.flowershoptr.dto.flower.CreateFlowerDTO;
import com.example.flowershoptr.dto.flower.FlowerDetailsDTO;
import com.example.flowershoptr.dto.flower.FlowerListDTO;
import com.example.flowershoptr.dto.flower.UpdateFlowerDTO;
import com.example.flowershoptr.service.CategoryService;
import com.example.flowershoptr.service.FlowerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Контроллер для административного управления цветами
 */
@Controller
@RequestMapping("/admin/flowers")
@RequiredArgsConstructor
@Slf4j
public class AdminFlowerController {

    private final FlowerService flowerService;
    private final CategoryService categoryService;

    /**
     * Отображение списка всех цветов с пагинацией
     */
    @GetMapping
    public String getAllFlowers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String keyword,
            Model model) {

        log.info("Отображение списка цветов. Страница: {}, размер: {}, сортировка: {}, направление: {}, поиск: {}",
                page, size, sortBy, sortDir, keyword);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<FlowerListDTO> flowersPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            flowersPage = flowerService.searchFlowersByName(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            flowersPage = flowerService.getAllFlowers(pageable);
        }

        model.addAttribute("flowers", flowersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", flowersPage.getTotalPages());
        model.addAttribute("totalItems", flowersPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("size", size);

        return "admin/flowers/list";
    }

    /**
     * Отображение страницы с детальной информацией о цветке
     */
    @GetMapping("/{id}")
    public String getFlowerDetails(@PathVariable Long id, Model model) {
        log.info("Запрос детальной информации о цветке с ID: {}", id);

        FlowerDetailsDTO flower = flowerService.getFlowerById(id);
        model.addAttribute("flower", flower);

        return "admin/flowers/details";
    }

    /**
     * Отображение формы для создания нового цветка
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        log.info("Отображение формы для создания нового цветка");

        model.addAttribute("flowerDTO", new CreateFlowerDTO());
        model.addAttribute("categories", categoryService.getAllCategoriesToAdmin());

        return "admin/flowers/create";
    }

    /**
     * Обработка создания нового цветка
     */
    @PostMapping("/create")
    public String createFlower(
            @Valid @ModelAttribute("flowerDTO") CreateFlowerDTO flowerDTO,
            BindingResult bindingResult,
            @RequestParam Long categoryId,
            @RequestParam(required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model) {

        log.info("Запрос на создание нового цветка в категории с ID: {}", categoryId);

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при создании цветка: {}", bindingResult.getAllErrors());
            model.addAttribute("categories", categoryService.getAllCategoriesToAdmin());
            return "admin/flowers/create";
        }

        try {
            FlowerDetailsDTO createdFlower;

            if (imageFile != null && !imageFile.isEmpty()) {
                createdFlower = flowerService.createFlowerWithImage(flowerDTO, categoryId, imageFile);
                log.info("Цветок успешно создан с изображением, ID: {}", createdFlower.getId());
            } else {
                createdFlower = flowerService.createFlower(flowerDTO, categoryId);
                log.info("Цветок успешно создан без изображения, ID: {}", createdFlower.getId());
            }

            redirectAttributes.addFlashAttribute("successMessage",
                    "Цветок \"" + createdFlower.getName() + "\" успешно создан");

            return "redirect:/admin/flowers";
        } catch (Exception e) {
            log.error("Ошибка при создании цветка: {}", e.getMessage());
            model.addAttribute("errorMessage", "Ошибка при создании цветка: " + e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategoriesToAdmin());
            return "admin/flowers/create";
        }
    }

    /**
     * Отображение формы для редактирования цветка
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        log.info("Отображение формы для редактирования цветка с ID: {}", id);

        FlowerDetailsDTO flower = flowerService.getFlowerById(id);

        // Создаем UpdateFlowerDTO из FlowerDetailsDTO
        UpdateFlowerDTO updateDTO = new UpdateFlowerDTO();
        updateDTO.setName(flower.getName());
        updateDTO.setFullDescription(flower.getFullDescription());
        updateDTO.setShortDescription(flower.getShortDescription());
        updateDTO.setPreviewImageUrl(flower.getPreviewImageUrl());
        updateDTO.setPhotoId(flower.getPhotoId());
        updateDTO.setCount(flower.getCount());
        updateDTO.setPrice(flower.getPrice());
        updateDTO.setActive(flower.isActive());
        updateDTO.setHasDeliveryToday(flower.isHasDeliveryToday());
        updateDTO.setWeight(flower.getWeight());

        model.addAttribute("flowerDTO", updateDTO);
        model.addAttribute("flower", flower);
        model.addAttribute("categories", categoryService.getAllCategoriesToAdmin());
        model.addAttribute("selectedCategoryId", flower.getCategory() != null ? flower.getCategory().getId() : null);

        return "admin/flowers/edit";
    }

    /**
     * Обработка обновления цветка
     */
    @PostMapping("/{id}/edit")
    public String updateFlower(
            @PathVariable Long id,
            @Valid @ModelAttribute("flowerDTO") UpdateFlowerDTO flowerDTO,
            BindingResult bindingResult,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model) {

        log.info("Запрос на обновление цветка с ID: {}", id);

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при обновлении цветка: {}", bindingResult.getAllErrors());
            model.addAttribute("categories", categoryService.getAllCategoriesToAdmin());
            model.addAttribute("selectedCategoryId", categoryId);
            return "admin/flowers/edit";
        }

        try {
            FlowerDetailsDTO updatedFlower;

            if (imageFile != null && !imageFile.isEmpty()) {
                updatedFlower = flowerService.updateFlowerWithImage(id, flowerDTO, categoryId, imageFile);
                log.info("Цветок с ID: {} успешно обновлен с новым изображением", id);
            } else {
                updatedFlower = flowerService.updateFlower(id, flowerDTO, categoryId);
                log.info("Цветок с ID: {} успешно обновлен без изменения изображения", id);
            }

            redirectAttributes.addFlashAttribute("successMessage",
                    "Цветок \"" + updatedFlower.getName() + "\" успешно обновлен");

            return "redirect:/admin/flowers";
        } catch (Exception e) {
            log.error("Ошибка при обновлении цветка с ID {}: {}", id, e.getMessage());
            model.addAttribute("errorMessage", "Ошибка при обновлении цветка: " + e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategoriesToAdmin());
            model.addAttribute("selectedCategoryId", categoryId);
            return "admin/flowers/edit";
        }
    }

    /**
     * Обработка удаления цветка
     */
    @PostMapping("/{id}/delete")
    public String deleteFlower(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Запрос на удаление цветка с ID: {}", id);

        try {
            FlowerDetailsDTO flowerToDelete = flowerService.getFlowerById(id);
            boolean deleted = flowerService.deleteFlower(id);

            if (deleted) {
                log.info("Цветок с ID: {} успешно удален", id);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Цветок \"" + flowerToDelete.getName() + "\" успешно удален");
            } else {
                log.warn("Не удалось удалить цветок с ID: {}", id);
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Не удалось удалить цветок");
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении цветка с ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ошибка при удалении цветка: " + e.getMessage());
        }

        return "redirect:/admin/flowers";
    }

    /**
     * Обработка обновления статуса активности цветка
     */
    @PostMapping("/{id}/toggle-active")
    public String toggleActive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Запрос на изменение статуса активности цветка с ID: {}", id);

        try {
            FlowerDetailsDTO flower = flowerService.getFlowerById(id);

            UpdateFlowerDTO updateDTO = new UpdateFlowerDTO();
            updateDTO.setName(flower.getName());
            updateDTO.setFullDescription(flower.getFullDescription());
            updateDTO.setShortDescription(flower.getShortDescription());
            updateDTO.setPreviewImageUrl(flower.getPreviewImageUrl());
            updateDTO.setPhotoId(flower.getPhotoId());
            updateDTO.setCount(flower.getCount());
            updateDTO.setPrice(flower.getPrice());
            updateDTO.setActive(!flower.isActive()); // Инвертируем статус активности
            updateDTO.setHasDeliveryToday(flower.isHasDeliveryToday());
            updateDTO.setWeight(flower.getWeight());

            Long categoryId = flower.getCategory() != null ? flower.getCategory().getId() : null;
            FlowerDetailsDTO updatedFlower = flowerService.updateFlower(id, updateDTO, categoryId);

            String statusMessage = updatedFlower.isActive() ? "активирован" : "деактивирован";
            log.info("Цветок с ID: {} успешно {}", id, statusMessage);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Цветок \"" + updatedFlower.getName() + "\" успешно " + statusMessage);

        } catch (Exception e) {
            log.error("Ошибка при изменении статуса активности цветка с ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ошибка при изменении статуса активности цветка: " + e.getMessage());
        }

        return "redirect:/admin/flowers";
    }

    /**
     * Обработка обновления статуса доставки сегодня
     */
    @PostMapping("/{id}/toggle-delivery")
    public String toggleDeliveryToday(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Запрос на изменение статуса доставки сегодня для цветка с ID: {}", id);

        try {
            FlowerDetailsDTO flower = flowerService.getFlowerById(id);

            UpdateFlowerDTO updateDTO = new UpdateFlowerDTO();
            updateDTO.setName(flower.getName());
            updateDTO.setFullDescription(flower.getFullDescription());
            updateDTO.setShortDescription(flower.getShortDescription());
            updateDTO.setPreviewImageUrl(flower.getPreviewImageUrl());
            updateDTO.setPhotoId(flower.getPhotoId());
            updateDTO.setCount(flower.getCount());
            updateDTO.setPrice(flower.getPrice());
            updateDTO.setActive(flower.isActive());
            updateDTO.setHasDeliveryToday(!flower.isHasDeliveryToday()); // Инвертируем статус доставки
            updateDTO.setWeight(flower.getWeight());

            Long categoryId = flower.getCategory() != null ? flower.getCategory().getId() : null;
            FlowerDetailsDTO updatedFlower = flowerService.updateFlower(id, updateDTO, categoryId);

            String statusMessage = updatedFlower.isHasDeliveryToday() ?
                    "доступна доставка сегодня" : "доставка сегодня отключена";

            log.info("Цветок с ID: {} - {}", id, statusMessage);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Для цветка \"" + updatedFlower.getName() + "\" " + statusMessage);

        } catch (Exception e) {
            log.error("Ошибка при изменении статуса доставки для цветка с ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ошибка при изменении статуса доставки: " + e.getMessage());
        }

        return "redirect:/admin/flowers";
    }

    /**
     * Обработка обновления количества товара на складе
     */
    @PostMapping("/{id}/update-stock")
    public String updateStock(
            @PathVariable Long id,
            @RequestParam Integer newCount,
            RedirectAttributes redirectAttributes) {

        log.info("Запрос на обновление количества на складе для цветка с ID: {} на {}", id, newCount);

        try {
            FlowerDetailsDTO updatedFlower = flowerService.updateStock(id, newCount);

            log.info("Количество цветка с ID: {} обновлено до {}", id, newCount);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Количество цветка \"" + updatedFlower.getName() + "\" обновлено до " + newCount);

        } catch (Exception e) {
            log.error("Ошибка при обновлении количества для цветка с ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ошибка при обновлении количества: " + e.getMessage());
        }

        return "redirect:/admin/flowers";
    }
}