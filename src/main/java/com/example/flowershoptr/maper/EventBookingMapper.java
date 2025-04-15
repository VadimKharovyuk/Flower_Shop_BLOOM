package com.example.flowershoptr.mapper;

import com.example.flowershoptr.dto.eventBooking.BookingCreateDto;
import com.example.flowershoptr.dto.eventBooking.EventBookingDto;
import com.example.flowershoptr.dto.eventBooking.EventBookingListDto;
import com.example.flowershoptr.model.Event;
import com.example.flowershoptr.model.EventBooking;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventBookingMapper {

    /**
     * Преобразует EventBooking в EventBookingDto
     */
    public EventBookingDto toDto(EventBooking booking) {
        if (booking == null) {
            return null;
        }

        EventBookingDto dto = new EventBookingDto();
        dto.setId(booking.getId());

        if (booking.getEvent() != null) {
            dto.setEventId(booking.getEvent().getId());
            dto.setEventTitle(booking.getEvent().getTitle());
            dto.setEventDate(booking.getEvent().getEventDate());
        }

        dto.setStatus(booking.getStatus());
        dto.setCustomerName(booking.getCustomerName());
        dto.setCustomerEmail(booking.getCustomerEmail());
        dto.setCustomerPhone(booking.getCustomerPhone());
        dto.setNumberOfSeats(booking.getNumberOfSeats());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setSpecialRequests(booking.getSpecialRequests());
        dto.setCreatedAt(booking.getCreatedAt());

        return dto;
    }

    /**
     * Преобразует EventBooking в упрощенный EventBookingListDto для отображения в списках
     */
    public EventBookingListDto toListItemDto(EventBooking booking) {
        if (booking == null) {
            return null;
        }

        EventBookingListDto dto = new EventBookingListDto();
        dto.setId(booking.getId());

        if (booking.getEvent() != null) {
            dto.setEventId(booking.getEvent().getId());
            dto.setEventTitle(booking.getEvent().getTitle());
            dto.setEventDate(booking.getEvent().getEventDate());
        }

        dto.setStatus(booking.getStatus());
        dto.setCustomerName(booking.getCustomerName());
        dto.setCustomerEmail(booking.getCustomerEmail());
        dto.setNumberOfSeats(booking.getNumberOfSeats());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setCreatedAt(booking.getCreatedAt());

        return dto;
    }


    /**
     * Преобразует BookingCreateDto в EventBooking
     * Примечание: Event должен быть установлен отдельно
     */
    public EventBooking toEntity(BookingCreateDto dto) {
        if (dto == null) {
            return null;
        }

        EventBooking booking = new EventBooking();
        booking.setCustomerName(dto.getCustomerName());
        booking.setCustomerEmail(dto.getCustomerEmail());
        booking.setCustomerPhone(dto.getCustomerPhone());
        booking.setNumberOfSeats(dto.getNumberOfSeats() != null ? dto.getNumberOfSeats() : 1);
        booking.setSpecialRequests(dto.getSpecialRequests());

        return booking;
    }


}