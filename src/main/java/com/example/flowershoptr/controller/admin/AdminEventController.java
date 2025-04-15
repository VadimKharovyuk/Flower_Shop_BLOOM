package com.example.flowershoptr.controller.admin;

import com.example.flowershoptr.dto.Event.EventCreateUpdateDto;
import com.example.flowershoptr.dto.Event.EventDetailsDto;
import com.example.flowershoptr.dto.Event.EventListDto;
import com.example.flowershoptr.enums.EventType;
import com.example.flowershoptr.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.IOException;

@Controller
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public String listEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<EventListDto> eventsPage = eventService.getAllEvents(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "eventDate")));

        model.addAttribute("events", eventsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", eventsPage.getTotalPages());
        model.addAttribute("totalItems", eventsPage.getTotalElements());

        return "admin/events/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new EventCreateUpdateDto());
        model.addAttribute("eventTypes", EventType.values());
        return "admin/events/create";
    }

    @PostMapping("/create")
    public String createEvent(
            @Valid @ModelAttribute("event") EventCreateUpdateDto eventDto,
            BindingResult result,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("eventTypes", EventType.values());
            return "admin/events/create";
        }

        try {
            EventDetailsDto createdEvent = eventService.createEvent(eventDto, imageFile);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Событие успешно создано: " + createdEvent.getTitle());
            return "redirect:/admin/events";
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Ошибка при загрузке изображения: " + e.getMessage());
            model.addAttribute("eventTypes", EventType.values());
            return "admin/events/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        EventDetailsDto event = eventService.getEventById(id);

        // Преобразование к DTO для редактирования
        EventCreateUpdateDto editDto = new EventCreateUpdateDto();
        editDto.setId(event.getId());
        editDto.setTitle(event.getTitle());
        editDto.setDescription(event.getDescription());
        editDto.setEventType(event.getEventType());
        editDto.setEventDate(event.getEventDate());
        editDto.setEndTime(event.getEndTime());
        editDto.setDurationMinutes(event.getDurationMinutes());
        editDto.setPrice(event.getPrice());
        editDto.setFree(event.isFree());
        editDto.setAvailableSeats(event.getAvailableSeats());
        editDto.setFeatured(event.isFeatured());

        model.addAttribute("event", editDto);
        model.addAttribute("eventTypes", EventType.values());
        model.addAttribute("previewImageUrl", event.getPreviewImageUrl());

        return "admin/events/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateEvent(
            @PathVariable Long id,
            @Valid @ModelAttribute("event") EventCreateUpdateDto eventDto,
            BindingResult result,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("eventTypes", EventType.values());
            return "admin/events/edit";
        }

        try {
            EventDetailsDto updatedEvent = eventService.updateEvent(id, eventDto, imageFile);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Событие успешно обновлено: " + updatedEvent.getTitle());
            return "redirect:/admin/events";
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Ошибка при загрузке изображения: " + e.getMessage());
            model.addAttribute("eventTypes", EventType.values());
            return "admin/events/edit";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        eventService.deleteEvent(id);
        redirectAttributes.addFlashAttribute("successMessage", "Событие успешно удалено");
        return "redirect:/admin/events";
    }

    @PostMapping("/{id}/delete-image")
    public String deleteEventImage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean deleted = eventService.deleteEventImage(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Изображение события успешно удалено");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Не удалось удалить изображение события");
        }
        return "redirect:/admin/events/edit/" + id;
    }
}