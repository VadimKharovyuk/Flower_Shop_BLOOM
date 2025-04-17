package com.example.flowershoptr.dto.ShopReview;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopReviewCreateDTO {

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
    private String userName;

    private String city;

    @NotBlank(message = "Краткое описание не может быть пустым")
    @Size(min = 5, max = 120, message = "Краткое описание должно содержать от 5 до 60 символов")
    private String shoutDescription;

    @NotNull(message = "Оценка не может быть пустой")
    @Min(value = 1, message = "Оценка должна быть от 1 до 5")
    @Max(value = 5, message = "Оценка должна быть от 1 до 5")
    private Integer rating;

    private String previewImageUrl;

    private String imagePublicId;
}