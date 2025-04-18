package com.example.flowershoptr.dto.Order;

import com.example.flowershoptr.enums.PaymentMethod;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDTO {
    @NotEmpty(message = "Имя клиента обязательно")
    private String clientName;

    @NotEmpty(message = "Номер телефона обязателен")
    private String clientPhone;

    @Email(message = "Некорректный email")
    private String clientEmail;

    @NotEmpty(message = "Адрес доставки обязателен")
    @Size(max = 255, message = "Адрес доставки не должен превышать 255 символов")
    private String deliveryAddress;

    @NotNull(message = "Дата доставки обязательна")
    private LocalDateTime deliveryDate;

    private String notes;

    @NotNull(message = "Метод оплаты обязателен")
    private PaymentMethod paymentMethod;

    @NotNull(message = "ID корзины обязателен")
    private String sessionId;

//    @NotNull
//    private String orderNumber;

}