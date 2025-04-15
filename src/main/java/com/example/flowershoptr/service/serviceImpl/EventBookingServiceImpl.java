package com.example.flowershoptr.service.impl;

import com.example.flowershoptr.dto.eventBooking.BookingCreateDto;
import com.example.flowershoptr.dto.eventBooking.EventBookingDto;
import com.example.flowershoptr.dto.eventBooking.EventBookingListDto;
import com.example.flowershoptr.enums.BookingStatus;
import com.example.flowershoptr.exception.ResourceNotFoundException;
import com.example.flowershoptr.mapper.EventBookingMapper;
import com.example.flowershoptr.model.Event;
import com.example.flowershoptr.model.EventBooking;
import com.example.flowershoptr.repository.EventBookingRepository;
import com.example.flowershoptr.repository.EventRepository;
import com.example.flowershoptr.service.EventBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventBookingServiceImpl implements EventBookingService {

    private final EventBookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final EventBookingMapper bookingMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EventBookingListDto> getAllBookings() {
        List<EventBooking> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(bookingMapper::toListItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventBookingListDto> getBookingsByEventId(Long eventId) {
        List<EventBooking> bookings = bookingRepository.findByEventId(eventId);
        return bookings.stream()
                .map(bookingMapper::toListItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventBookingListDto> getBookingsByEmail(String email) {
        List<EventBooking> bookings = bookingRepository.findByCustomerEmail(email);
        return bookings.stream()
                .map(bookingMapper::toListItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventBookingListDto> getBookingsByStatus(BookingStatus status) {
        List<EventBooking> bookings = bookingRepository.findByStatus(status);
        return bookings.stream()
                .map(bookingMapper::toListItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventBookingListDto> getBookingsByEventIdAndStatus(Long eventId, BookingStatus status) {
        List<EventBooking> bookings = bookingRepository.findByEventIdAndStatus(eventId, status);
        return bookings.stream()
                .map(bookingMapper::toListItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventBookingDto getBookingById(Long id) {
        EventBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Бронирование не найдено с id: " + id));
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public EventBookingDto createBooking(BookingCreateDto dto) {
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Событие не найдено с id: " + dto.getEventId()));

        // Проверка доступности мест
        if (!areSeatsAvailable(event.getId(), dto.getNumberOfSeats())) {
            throw new IllegalStateException("Недостаточно мест для бронирования");
        }

        EventBooking booking = bookingMapper.toEntity(dto);
        booking.setEvent(event);
        booking.setStatus(BookingStatus.PENDING);

        // Расчет общей стоимости
        BigDecimal totalPrice = calculateTotalPrice(event, dto.getNumberOfSeats());
        booking.setTotalPrice(totalPrice);

        EventBooking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    @Override
    @Transactional
    public EventBookingDto updateBookingStatus(Long id, BookingStatus status) {
        EventBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Бронирование не найдено с id: " + id));

        booking.setStatus(status);
        EventBooking updatedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(updatedBooking);
    }

    @Override
    @Transactional
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Бронирование не найдено с id: " + id);
        }
        bookingRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countBookingsByEventIdAndStatus(Long eventId, BookingStatus status) {
        return bookingRepository.countByEventIdAndStatus(eventId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean areSeatsAvailable(Long eventId, Integer requestedSeats) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Событие не найдено с id: " + eventId));

        // Если количество мест не ограничено
        if (event.getAvailableSeats() == null) {
            return true;
        }

        // Количество подтвержденных бронирований
        long confirmedSeats = bookingRepository.findByEventIdAndStatus(eventId, BookingStatus.CONFIRMED)
                .stream()
                .mapToInt(EventBooking::getNumberOfSeats)
                .sum();

        return confirmedSeats + requestedSeats <= event.getAvailableSeats();
    }

    /**
     * Рассчитывает общую стоимость бронирования на основе цены события и количества мест
     */
    private BigDecimal calculateTotalPrice(Event event, Integer numberOfSeats) {
        if (event == null || event.getPrice() == null || numberOfSeats == null) {
            return BigDecimal.ZERO;
        }

        return event.getPrice().multiply(BigDecimal.valueOf(numberOfSeats));
    }
}