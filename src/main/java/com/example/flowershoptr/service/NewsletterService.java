package com.example.flowershoptr.service;

import com.example.flowershoptr.dto.Subscrip.NewsletterRequest;
import com.example.flowershoptr.model.BlogPost;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsletterService {

    private final SubscriptionService subscriptionService;
    private final EmailService emailService;
    private final BlogPostService blogPostService;

    /**
     * Отправляет рассылку с информацией о новом посте в блоге
     *
     * @param blogPostId ID поста для рассылки
     * @return true, если рассылка прошла успешно
     */
    @Transactional(readOnly = true)
    public boolean sendBlogPostNewsletter(Long blogPostId) {
        List<String> subscriberEmails = subscriptionService.getActiveSubscriberEmails();

        if (subscriberEmails.isEmpty()) {
            log.warn("Попытка отправки рассылки о посте при отсутствии подписчиков");
            return false;
        }

        Optional<BlogPost> optionalBlogPost = blogPostService.getBlogPostByIdOptional(blogPostId);
        if (!optionalBlogPost.isPresent()) {
            log.error("Попытка отправки рассылки с несуществующим постом ID: {}", blogPostId);
            return false;
        }

        BlogPost blogPost = optionalBlogPost.get();

        Map<String, Object> context = new HashMap<>();
        context.put("post", blogPost);
        context.put("postUrl", "/blog/" + blogPost.getId());
        context.put("subscriberCount", subscriberEmails.size());

        log.info("Начало рассылки о новом посте блога: {}, Количество получателей: {}",
                blogPost.getTitle(), subscriberEmails.size());

        try {
            emailService.sendBulkEmail(
                    subscriberEmails,
                    "Новый пост в блоге: " + blogPost.getTitle(),
                    "blog-post",
                    context
            );
            return true;
        } catch (Exception e) {
            log.error("Ошибка при отправке рассылки о посте: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Отправляет кастомную рассылку всем активным подписчикам
     *
     * @param request Запрос с данными для рассылки
     * @return true, если рассылка прошла успешно
     */
    @Transactional(readOnly = true)
    public boolean sendCustomNewsletter(NewsletterRequest request) {
        List<String> subscriberEmails = subscriptionService.getActiveSubscriberEmails();

        if (subscriberEmails.isEmpty()) {
            log.warn("Попытка отправки кастомной рассылки при отсутствии подписчиков");
            return false;
        }

        Map<String, Object> context = new HashMap<>();
        context.put("message", request.getContent());
        context.put("subscriberCount", subscriberEmails.size());

        // Если указан ID поста блога, добавляем информацию о нем
        if (request.getBlogPostId() != null) {
            Optional<BlogPost> optionalBlogPost = blogPostService.getBlogPostByIdOptional(request.getBlogPostId());
            optionalBlogPost.ifPresent(blogPost -> {
                context.put("post", blogPost);
                context.put("postUrl", "/blog/" + blogPost.getId());
            });
        }

        log.info("Начало кастомной рассылки. Тема: {}, Количество получателей: {}",
                request.getSubject(), subscriberEmails.size());

        try {
            emailService.sendBulkEmail(
                    subscriberEmails,
                    request.getSubject(),
                    "custom-message",
                    context
            );
            return true;
        } catch (Exception e) {
            log.error("Ошибка при отправке кастомной рассылки: {}", e.getMessage());
            return false;
        }
    }




}