package com.example.flowershoptr.service;

import com.example.flowershoptr.dto.eventBooking.BookingCreateDto;
import com.example.flowershoptr.dto.eventBooking.EventBookingDto;
import com.example.flowershoptr.dto.eventBooking.EventBookingListDto;
import com.example.flowershoptr.enums.BookingStatus;

import java.util.List;

/**
 * Интерфейс сервиса для работы с бронированиями событий
 */
public interface EventBookingService {

    /**
     * Получение всех бронирований в формате списка
     */
    List<EventBookingListDto> getAllBookings();

    /**
     * Получение бронирований по идентификатору события
     */
    List<EventBookingListDto> getBookingsByEventId(Long eventId);

    /**
     * Получение бронирований по email клиента
     */
    List<EventBookingListDto> getBookingsByEmail(String email);

    /**
     * Получение бронирований по статусу
     */
    List<EventBookingListDto> getBookingsByStatus(BookingStatus status);

    /**
     * Получение бронирований по идентификатору события и статусу
     */
    List<EventBookingListDto> getBookingsByEventIdAndStatus(Long eventId, BookingStatus status);

    /**
     * Получение детальной информации о бронировании по ID
     */
    EventBookingDto getBookingById(Long id);

    /**
     * Создание нового бронирования
     */
    EventBookingDto createBooking(BookingCreateDto dto);

    /**
     * Обновление статуса бронирования
     */
    EventBookingDto updateBookingStatus(Long id, BookingStatus status);

    /**
     * Удаление бронирования
     */
    void deleteBooking(Long id);

    /**
     * Подсчет количества бронирований для события по статусу
     */
    long countBookingsByEventIdAndStatus(Long eventId, BookingStatus status);

    /**
     * Проверка доступности мест для бронирования
     */
    boolean areSeatsAvailable(Long eventId, Integer requestedSeats);
}