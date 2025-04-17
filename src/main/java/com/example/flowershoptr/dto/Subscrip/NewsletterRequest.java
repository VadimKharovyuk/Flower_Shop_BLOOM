package com.example.flowershoptr.dto.Subscrip;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsletterRequest {

    @NotBlank(message = "Тема не может быть пустой")
    private String subject;

    @NotBlank(message = "Содержание не может быть пустым")
    private String content;

    private Long blogPostId;
}