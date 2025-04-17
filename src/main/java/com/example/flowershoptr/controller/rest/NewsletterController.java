package com.example.flowershoptr.controller.rest;

import com.example.flowershoptr.dto.Subscrip.NewsletterRequest;
import com.example.flowershoptr.service.NewsletterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/newsletters")
@RequiredArgsConstructor
@Slf4j
public class NewsletterController {

    private final NewsletterService newsletterService;

    /**
     * Отправка кастомной рассылки всем активным подписчикам
     *
     * @param request Запрос с данными для рассылки
     * @return Статус операции
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendCustomNewsletter(@Valid @RequestBody NewsletterRequest request) {
        try {
            boolean sent = newsletterService.sendCustomNewsletter(request);

            if (sent) {
                return ResponseEntity.ok("Рассылка успешно отправлена всем активным подписчикам");
            } else {
                return ResponseEntity.badRequest().body("Не удалось отправить рассылку");
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке рассылки: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Ошибка при отправке рассылки: " + e.getMessage());
        }
    }

    /**
     * Отправка рассылки о новом посте в блоге
     *
     * @param blogPostId ID поста в блоге
     * @return Статус операции
     */
    @PostMapping("/send-blog-post/{blogPostId}")
    public ResponseEntity<?> sendBlogPostNewsletter(@PathVariable Long blogPostId) {
        try {
            boolean sent = newsletterService.sendBlogPostNewsletter(blogPostId);

            if (sent) {
                return ResponseEntity.ok("Рассылка о новом посте успешно отправлена всем активным подписчикам");
            } else {
                return ResponseEntity.badRequest().body("Не удалось отправить рассылку о посте");
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке рассылки о посте: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Ошибка при отправке рассылки: " + e.getMessage());
        }
    }
}