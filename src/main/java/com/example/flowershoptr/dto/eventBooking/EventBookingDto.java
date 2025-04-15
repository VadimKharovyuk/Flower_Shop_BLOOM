package com.example.flowershoptr.dto.eventBooking;

import com.example.flowershoptr.enums.BookingStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EventBookingDto {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private LocalDateTime eventDate;
    private BookingStatus status;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private Integer numberOfSeats;
    private BigDecimal totalPrice;
    private String specialRequests;
    private LocalDateTime createdAt;
}
