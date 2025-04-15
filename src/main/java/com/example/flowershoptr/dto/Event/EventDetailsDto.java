package com.example.flowershoptr.dto.Event;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventDetailsDto {
    private Long id;
    private String title;
    private String description;
    private String eventType;
    private LocalDateTime eventDate;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private BigDecimal price;
    private boolean isFree;
    private String previewImageUrl;
    private String imagePublicId;
    private boolean isFeatured;
    private boolean isActive;
    private Integer availableSeats;
    private Integer bookedSeats;
    private Integer remainingSeats;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<EventBookingSimpleDto> bookings;

    // Вложенный класс для упрощенного представления бронирований
    @Data
    public static class EventBookingSimpleDto {
        private Long id;
        private String customerName;
        private String status;
        private Integer numberOfSeats;
    }
}