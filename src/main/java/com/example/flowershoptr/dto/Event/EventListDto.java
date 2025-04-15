package com.example.flowershoptr.dto.Event;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EventListDto {
    private Long id;
    private String title;
    private String eventType;
    private LocalDateTime eventDate;
    private LocalDateTime endTime;
    private BigDecimal price;
    private boolean isFree;
    private String previewImageUrl;
    private boolean isFeatured;
    private Integer availableSeats;
}