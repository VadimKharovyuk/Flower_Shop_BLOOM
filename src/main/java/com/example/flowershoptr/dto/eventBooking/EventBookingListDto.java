package com.example.flowershoptr.dto.eventBooking;

import com.example.flowershoptr.enums.BookingStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO для отображения бронирований в списке
 * Содержит меньше полей, чем полный EventBookingDto
 */
@Data
public class EventBookingListDto {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private LocalDateTime eventDate;
    private BookingStatus status;
    private String customerName;
    private String customerEmail;
    private Integer numberOfSeats;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
}