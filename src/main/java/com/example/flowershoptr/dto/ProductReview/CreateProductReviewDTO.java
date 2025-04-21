package com.example.flowershoptr.dto.ProductReview;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateProductReviewDTO {
    @NotNull(message = "ID цветка обязателен")
    private Long flowerId;

    @NotNull(message = "Рейтинг обязателен")
    @Min(value = 1, message = "Рейтинг должен быть от 1 до 5")
    @Max(value = 5, message = "Рейтинг должен быть от 1 до 5")
    private Integer rating;

    @Size(max = 500, message = "Комментарий не должен превышать 500 символов")
    private String comment;
}