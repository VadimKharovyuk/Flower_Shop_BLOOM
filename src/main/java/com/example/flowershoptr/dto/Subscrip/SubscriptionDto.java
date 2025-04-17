package com.example.flowershoptr.dto.Subscrip;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDto {

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Пожалуйста, введите корректный email")
    private String email;
}