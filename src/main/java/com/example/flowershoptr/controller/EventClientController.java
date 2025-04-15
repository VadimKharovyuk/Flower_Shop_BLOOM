package com.example.flowershoptr.controller;

import com.example.flowershoptr.dto.Event.EventListDto;
import com.example.flowershoptr.dto.eventBooking.BookingCreateDto;
import com.example.flowershoptr.dto.eventBooking.EventBookingDto;
import com.example.flowershoptr.service.EventBookingService;
import com.example.flowershoptr.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Контроллер для клиентской части работы с событиями и бронированиями
 */
@Controller
@RequiredArgsConstructor
public class EventClientController {

    private final EventService eventService;
    private final EventBookingService bookingService;


    @GetMapping("/events")
    public String showEventsList(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<EventListDto> eventListDtos = eventService.getAllEvents(pageable);

        model.addAttribute("eventListDtos", eventListDtos);
        model.addAttribute("currentPage", "events");

        return "client/events/list";
    }
    /**
     * Отображение детальной информации о событии и формы бронирования
     */
    @GetMapping("/events/{id}")
    public String showEventDetails(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.getEventById(id));

        // Создаем пустую форму для бронирования
        BookingCreateDto bookingForm = new BookingCreateDto();
        bookingForm.setEventId(id);
        bookingForm.setNumberOfSeats(1); // По умолчанию 1 место
        model.addAttribute("bookingForm", bookingForm);

        return "client/events/details";
    }

    /**
     * Обработка запроса на бронирование события
     */
    @PostMapping("/events/booking")
    public String createBooking(
            @Valid @ModelAttribute("bookingForm") BookingCreateDto bookingForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Если есть ошибки валидации, возвращаемся на форму
        if (bindingResult.hasErrors()) {
            model.addAttribute("event", eventService.getEventById(bookingForm.getEventId()));
            return "client/events/details";
        }

        try {
            // Проверка доступности мест
            if (!bookingService.areSeatsAvailable(bookingForm.getEventId(), bookingForm.getNumberOfSeats())) {
                model.addAttribute("event", eventService.getEventById(bookingForm.getEventId()));
                model.addAttribute("errorMessage", "К сожалению, недостаточно свободных мест для вашего бронирования");
                return "client/events/details";
            }

            // Создаем бронирование
            EventBookingDto createdBooking = bookingService.createBooking(bookingForm);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Ваше бронирование успешно создано! Номер бронирования: " + createdBooking.getId());

            return "redirect:/events/booking/confirmation/" + createdBooking.getId();

        } catch (Exception e) {
            // Обработка ошибок
            model.addAttribute("event", eventService.getEventById(bookingForm.getEventId()));
            model.addAttribute("errorMessage", "Произошла ошибка: " + e.getMessage());
            return "client/events/details";
        }
    }

    /**
     * Отображение подтверждения бронирования
     */
    @GetMapping("/events/booking/confirmation/{id}")
    public String showBookingConfirmation(@PathVariable Long id, Model model) {
        model.addAttribute("booking", bookingService.getBookingById(id));
        return "client/events/booking-confirmation";
    }

    /**
     * Отображение формы для проверки статуса бронирования по email
     */
    @GetMapping("/events/booking/check")
    public String showBookingCheckForm() {
        return "client/events/booking-check";
    }

    /**
     * Отображение списка бронирований пользователя по email
     */
    @GetMapping("/events/booking/my")
    public String showMyBookings(@RequestParam String email, Model model) {
        model.addAttribute("bookings", bookingService.getBookingsByEmail(email));
        model.addAttribute("email", email);
        return "client/events/my-bookings";
    }

    /**
     * Отображение конкретного бронирования пользователя
     */
    @GetMapping("/events/booking/{id}")
    public String showBookingDetails(@PathVariable Long id, Model model) {
        model.addAttribute("booking", bookingService.getBookingById(id));
        return "client/events/booking-details";
    }
}