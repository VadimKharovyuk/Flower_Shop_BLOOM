package com.example.flowershoptr.maper;

import com.example.flowershoptr.dto.Event.EventCreateUpdateDto;
import com.example.flowershoptr.dto.Event.EventDetailsDto;
import com.example.flowershoptr.dto.Event.EventListDto;
import com.example.flowershoptr.enums.BookingStatus;
import com.example.flowershoptr.model.Event;
import com.example.flowershoptr.model.EventBooking;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventMapper {

    // Преобразование Entity в ListDto
    public EventListDto toListDto(Event event) {
        EventListDto dto = new EventListDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setEventType(event.getEventType());
        dto.setEventDate(event.getEventDate());
        dto.setEndTime(event.getEndTime());
        dto.setPrice(event.getPrice());
        dto.setFree(event.isFree());
        dto.setPreviewImageUrl(event.getPreviewImageUrl());
        dto.setFeatured(event.isFeatured());
        dto.setAvailableSeats(event.getAvailableSeats());
        dto.setDescription(event.getDescription());
        return dto;
    }

    // Преобразование списка Entity в список ListDto
    public List<EventListDto> toListDto(List<Event> events) {
        return events.stream()
                .map(this::toListDto)
                .collect(Collectors.toList());
    }

    // Преобразование Entity в DetailsDto
    public EventDetailsDto toDetailsDto(Event event) {
        EventDetailsDto dto = new EventDetailsDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setEventType(event.getEventType());
        dto.setEventDate(event.getEventDate());
        dto.setEndTime(event.getEndTime());
        dto.setDurationMinutes(event.getDurationMinutes());
        dto.setPrice(event.getPrice());
        dto.setFree(event.isFree());
        dto.setPreviewImageUrl(event.getPreviewImageUrl());
        dto.setImagePublicId(event.getImagePublicId());

        dto.setFeatured(event.isFeatured());
        dto.setActive(event.isActive());
        dto.setAvailableSeats(event.getAvailableSeats());

        // Расчет забронированных мест
        int bookedSeats = 0;
        for (EventBooking booking : event.getBookings()) {
            if (booking.getStatus() != BookingStatus.CANCELLED) {
                bookedSeats += booking.getNumberOfSeats();
            }
        }
        dto.setBookedSeats(bookedSeats);
        dto.setRemainingSeats(event.getAvailableSeats() - bookedSeats);

        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());

        // Преобразование бронирований
        if (event.getBookings() != null) {
            dto.setBookings(event.getBookings().stream()
                    .map(booking -> {
                        EventDetailsDto.EventBookingSimpleDto bookingDto = new EventDetailsDto.EventBookingSimpleDto();
                        bookingDto.setId(booking.getId());
                        bookingDto.setCustomerName(booking.getCustomerName());
                        bookingDto.setStatus(booking.getStatus().name());
                        bookingDto.setNumberOfSeats(booking.getNumberOfSeats());
                        return bookingDto;
                    })
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    // Преобразование CreateUpdateDto в Entity
    public Event toEntity(EventCreateUpdateDto dto) {
        Event event = new Event();
        return updateEntityFromDto(event, dto);
    }

    // Обновление Entity из CreateUpdateDto
    public Event updateEntityFromDto(Event event, EventCreateUpdateDto dto) {
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setEventType(dto.getEventType());
        event.setEventDate(dto.getEventDate());
        event.setDurationMinutes(dto.getDurationMinutes());
        event.setEndTime(dto.getEndTime());
        event.setPrice(dto.getPrice());
        event.setFree(dto.isFree());
        event.setPreviewImageUrl(dto.getPreviewImageUrl());
        event.setImagePublicId(dto.getImagePublicId());
        event.setFeatured(dto.isFeatured());
        event.setAvailableSeats(dto.getAvailableSeats());

        // Логика для установки isFree на основе цены
        if (dto.getPrice() == null || dto.getPrice().doubleValue() == 0) {
            event.setFree(true);
        } else {
            event.setFree(false);
        }

        return event;
    }
}