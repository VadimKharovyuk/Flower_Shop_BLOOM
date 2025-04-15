package com.example.flowershoptr.dto.eventBooking;

import jakarta.validation.constraints.*;
import lombok.Data;


@Data
public class BookingCreateDto {

    @NotNull(message = "ID события обязателен")
    private Long eventId;

    @NotBlank(message = "Имя клиента обязательно")
    @Size(max = 100, message = "Имя не должно превышать 100 символов")
    private String customerName;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    private String customerEmail;

    @NotBlank(message = "Телефон обязателен")
    @Pattern(regexp = "^\\+?[0-9\\-\\s]{7,20}$", message = "Некорректный номер телефона")
    private String customerPhone;

    @NotNull(message = "Количество мест обязательно")
    @Min(value = 1, message = "Минимум одно место")
    @Max(value = 100, message = "Максимум 100 мест")
    private Integer numberOfSeats = 1;

    @Size(max = 2000, message = "Специальные пожелания не должны превышать 2000 символов")
    private String specialRequests;
}