package com.example.flowershoptr.controller.admin;

import com.example.flowershoptr.dto.Subscrip.NewsletterRequest;
import com.example.flowershoptr.model.BlogPost;
import com.example.flowershoptr.model.Subscription;
import com.example.flowershoptr.service.BlogPostService;
import com.example.flowershoptr.service.NewsletterService;
import com.example.flowershoptr.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/newsletter")
@RequiredArgsConstructor
@Slf4j
public class AdminNewsletterController {

    private final BlogPostService blogPostService;
    private final SubscriptionService subscriptionService;
    private final NewsletterService newsletterService;

    /**
     * Отображение главной страницы управления рассылками
     */
    @GetMapping
    public String showNewsletterDashboard(Model model) {
        List<BlogPost> blogPosts = blogPostService.getAllBlogPosts();
        long subscriberCount = subscriptionService.getActiveSubscriberCount();

        model.addAttribute("blogPosts", blogPosts);
        model.addAttribute("subscriberCount", subscriberCount);
        model.addAttribute("newsletterRequest", new NewsletterRequest());

        return "admin/newsletter/dashboard";
    }

    /**
     * Поиск постов по названию
     */
    @GetMapping("/search")
    public String searchPosts(@RequestParam("query") String query, Model model) {
        List<BlogPost> searchResults = blogPostService.searchBlogPostsByTitle(query);
        long subscriberCount = subscriptionService.getActiveSubscriberCount();

        model.addAttribute("blogPosts", searchResults);
        model.addAttribute("subscriberCount", subscriberCount);
        model.addAttribute("newsletterRequest", new NewsletterRequest());
        model.addAttribute("searchQuery", query);

        return "admin/newsletter/dashboard";
    }

    /**
     * Отображение страницы со списком подписчиков
     */
    @GetMapping("/subscribers")
    public String showSubscribers(Model model) {
        List<Subscription> subscribers = subscriptionService.getActiveSubscriptions();
        model.addAttribute("subscribers", subscribers);

        return "admin/newsletter/subscribers";
    }

    /**
     * Отправка рассылки о новом посте
     */
    @PostMapping("/send-post/{blogPostId}")
    public String sendBlogPostNewsletter(@PathVariable Long blogPostId, RedirectAttributes redirectAttributes) {
        try {
            boolean sent = newsletterService.sendBlogPostNewsletter(blogPostId);

            if (sent) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Рассылка о посте успешно отправлена всем активным подписчикам");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Не удалось отправить рассылку. Проверьте журнал приложения для получения дополнительной информации.");
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке рассылки о посте: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Произошла ошибка при отправке рассылки: " + e.getMessage());
        }

        return "redirect:/admin/newsletter";
    }

    /**
     * Отправка кастомной рассылки
     */
    @PostMapping("/send-custom")
    public String sendCustomNewsletter(@ModelAttribute NewsletterRequest request, RedirectAttributes redirectAttributes) {
        try {
            boolean sent = newsletterService.sendCustomNewsletter(request);

            if (sent) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Кастомная рассылка успешно отправлена всем активным подписчикам");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Не удалось отправить рассылку. Проверьте журнал приложения для получения дополнительной информации.");
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке кастомной рассылки: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Произошла ошибка при отправке рассылки: " + e.getMessage());
        }

        return "redirect:/admin/newsletter";
    }
}