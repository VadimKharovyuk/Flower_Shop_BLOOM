package com.example.flowershoptr.dto.Event;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EventCreateUpdateDto {
    private Long id; // Необходим только для обновления

    @NotBlank(message = "Название события обязательно")
    private String title;

    @NotBlank(message = "Описание события обязательно")
    private String description;

    @NotBlank(message = "Тип события обязателен")
    private String eventType;

    @NotNull(message = "Дата начала события обязательна")
    private LocalDateTime eventDate;

    @NotNull(message = "Продолжительность события обязательна")
    @Min(value = 1, message = "Продолжительность должна быть не менее 1 минуты")
    private Integer durationMinutes;

    private LocalDateTime endTime;

    private BigDecimal price;

    private boolean isFree;

    private String previewImageUrl;

    private String imagePublicId;

    private boolean isFeatured;

    @NotNull(message = "Количество доступных мест обязательно")
    @Min(value = 1, message = "Должно быть как минимум 1 место")
    private Integer availableSeats;




}