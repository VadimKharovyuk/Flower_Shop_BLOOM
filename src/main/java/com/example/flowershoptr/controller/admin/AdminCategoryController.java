package com.example.flowershoptr.controller.admin;

import com.example.flowershoptr.dto.category.CategoryDetailsDTO;
import com.example.flowershoptr.dto.category.CategoryListDTO;
import com.example.flowershoptr.dto.category.CreateCategoryDTO;
import com.example.flowershoptr.dto.category.UpdateCategoryDTO;
import com.example.flowershoptr.service.CategoryService;
import com.example.flowershoptr.service.serviceImpl.CategoryServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryController {

    private final CategoryService categoryService;
    private final CategoryServiceImpl categoryServiceImpl;

    /**
     * Отображение списка категорий
     */
    @GetMapping
    public String listCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending,
            Model model) {

        Sort sort = Sort.by(ascending ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CategoryListDTO> categories = categoryService.getAllCategories(pageable);

        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categories.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("ascending", ascending);

        return "admin/categories/list";
    }

    /**
     * Отображение формы для создания новой категории
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new CreateCategoryDTO());
        return "admin/categories/create";
    }


    @PostMapping("/create")
    public String createCategory(
            @ModelAttribute("category") CreateCategoryDTO createDTO,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {

        try {
            // Вызываем метод сервиса, который обрабатывает как данные, так и изображение
            CategoryDetailsDTO created = categoryService.createCategoryWithImage(createDTO, imageFile);

            redirectAttributes.addFlashAttribute("success",
                    "Категория \"" + created.getName() + "\" успешно создана!");

            // Проверяем, было ли загружено изображение
            if (imageFile != null && !imageFile.isEmpty() && created.getPreviewImageUrl() == null) {
                redirectAttributes.addFlashAttribute("warning",
                        "Категория создана, но возникла проблема с загрузкой изображения.");
            }

            return "redirect:/admin/categories";
        } catch (Exception e) {
            log.error("Ошибка при создании категории: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Ошибка при создании категории: " + e.getMessage());
            return "redirect:/admin/categories/create";
        }
    }

    /**
     * Отображение формы для редактирования категории
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        CategoryDetailsDTO category = categoryService.getCategoryById(id);

        // Преобразуем CategoryDetailsDTO в UpdateCategoryDTO
        UpdateCategoryDTO updateDTO = new UpdateCategoryDTO();
        updateDTO.setId(category.getId());
        updateDTO.setName(category.getName());
        updateDTO.setDescription(category.getDescription());
        updateDTO.setPreviewImageUrl(category.getPreviewImageUrl());
        updateDTO.setPhotoId(category.getPhotoId());
        updateDTO.setActive(category.isActive());
        updateDTO.setFeatured(category.isFeatured()); // Исправлено - напрямую вызываем isFeatured()

        model.addAttribute("category", updateDTO);
        return "admin/categories/edit";
    }

    /**
     * Обработка формы редактирования категории
     */
    @PostMapping("/edit/{id}")
    public String updateCategory(
            @PathVariable Long id,
            @ModelAttribute("category") UpdateCategoryDTO updateDTO,
            RedirectAttributes redirectAttributes) {

        try {
            CategoryDetailsDTO updated = categoryService.updateCategory(id, updateDTO);
            redirectAttributes.addFlashAttribute("success",
                    "Категория \"" + updated.getName() + "\" успешно обновлена!");
            return "redirect:/admin/categories";
        } catch (Exception e) {
            log.error("Ошибка при обновлении категории {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Ошибка при обновлении категории: " + e.getMessage());
            return "redirect:/admin/categories/edit/" + id;
        }
    }

    /**
     * Загрузка изображения для категории
     */
    @PostMapping("/{id}/upload-image")
    public String uploadCategoryImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        try {
            CategoryDetailsDTO updated = categoryServiceImpl.uploadCategoryImage(id, file);
            redirectAttributes.addFlashAttribute("success",
                    "Изображение для категории \"" + updated.getName() + "\" успешно загружено!");
        } catch (Exception e) {
            log.error("Ошибка при загрузке изображения для категории {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Ошибка при загрузке изображения: " + e.getMessage());
        }

        return "redirect:/admin/categories/edit/" + id;
    }

    /**
     * Изменение статуса активности категории
     */
    @PostMapping("/{id}/toggle-active")
    public String toggleCategoryActive(
            @PathVariable Long id,
            @RequestParam("active") boolean active,
            RedirectAttributes redirectAttributes) {

        try {
            CategoryDetailsDTO updated = categoryService.toggleCategoryActive(id, active);
            String status = active ? "активирована" : "деактивирована";
            redirectAttributes.addFlashAttribute("success",
                    "Категория \"" + updated.getName() + "\" успешно " + status + "!");
        } catch (Exception e) {
            log.error("Ошибка при изменении статуса активности категории {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Ошибка при изменении статуса активности: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    /**
     * Изменение статуса "featured" категории (показывать на главной)
     */
    @PostMapping("/{id}/toggle-featured")
    public String toggleCategoryFeatured(
            @PathVariable Long id,
            @RequestParam("featured") boolean featured,
            RedirectAttributes redirectAttributes) {

        try {
            // Получаем текущую категорию
            CategoryDetailsDTO category = categoryService.getCategoryById(id);

            // Создаем DTO для обновления
            UpdateCategoryDTO updateDTO = new UpdateCategoryDTO();
            updateDTO.setId(id);
            updateDTO.setName(category.getName());
            updateDTO.setDescription(category.getDescription());
            updateDTO.setPreviewImageUrl(category.getPreviewImageUrl());
            updateDTO.setPhotoId(category.getPhotoId());
            updateDTO.setActive(category.isActive());
            updateDTO.setFeatured(featured);

            // Обновляем категорию
            CategoryDetailsDTO updated = categoryService.updateCategory(id, updateDTO);

            String status = featured ? "добавлена на главную страницу" : "удалена с главной страницы";
            redirectAttributes.addFlashAttribute("success",
                    "Категория \"" + updated.getName() + "\" успешно " + status + "!");
        } catch (Exception e) {
            log.error("Ошибка при изменении статуса featured категории {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Ошибка при изменении статуса отображения на главной: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    /**
     * Удаление категории
     */
    @PostMapping("/{id}/delete")
    public String deleteCategory(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            // Получаем имя категории перед удалением для сообщения
            String categoryName = categoryService.getCategoryById(id).getName();

            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("success",
                    "Категория \"" + categoryName + "\" успешно удалена!");
        } catch (Exception e) {
            log.error("Ошибка при удалении категории {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Ошибка при удалении категории: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    /**
     * Просмотр деталей категории
     */
    @GetMapping("/view/{id}")
    public String viewCategory(@PathVariable Long id, Model model) {
        CategoryDetailsDTO category = categoryService.getCategoryById(id);
        model.addAttribute("category", category);
        return "admin/categories/view";
    }
}