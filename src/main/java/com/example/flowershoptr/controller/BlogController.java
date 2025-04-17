package com.example.flowershoptr.controller;

import com.example.flowershoptr.dto.BlogPost.BlogPostDetailsDto;
import com.example.flowershoptr.dto.BlogPost.BlogPostListDto;
import com.example.flowershoptr.enums.BlogPostType;
import com.example.flowershoptr.service.BlogPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/blog")
@RequiredArgsConstructor
public class BlogController {

    private final BlogPostService blogPostService;


    private List<String> getAllPostTypes() {
        return Arrays.stream(BlogPostType.values())
                .map(BlogPostType::name)
                .collect(Collectors.toList());
    }



    @GetMapping
    public String listBlogPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            Model model) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Page<BlogPostListDto> blogPosts;

        if (type != null && !type.isEmpty()) {
            blogPosts = blogPostService.getBlogPostsByType(type, PageRequest.of(page, size, sort));
            model.addAttribute("currentType", type);
        } else {
            blogPosts = blogPostService.getBlogPosts(PageRequest.of(page, size, sort));
        }

        // Используем приватный метод вместо дублирования кода
        model.addAttribute("postTypes", getAllPostTypes());
        model.addAttribute("blogPosts", blogPosts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", blogPosts.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);

        // Получаем популярные посты для боковой панели
        Page<BlogPostListDto> popularPosts = blogPostService.getMostPopularBlogPosts(
                PageRequest.of(0, 3, Sort.by("viewCount").descending()));
        model.addAttribute("popularPosts", popularPosts.getContent());

        return "client/blog/list";
    }

    // Остальные методы также используют приватный метод
    @GetMapping("/popular")
    public String getPopularBlogPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            Model model) {

        Page<BlogPostListDto> popularPosts = blogPostService.getMostPopularBlogPosts(
                PageRequest.of(page, size));

        // Используем приватный метод
        model.addAttribute("postTypes", getAllPostTypes());
        model.addAttribute("blogPosts", popularPosts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", popularPosts.getTotalPages());
        model.addAttribute("isPopular", true);

        // Получаем популярные посты для боковой панели
        Page<BlogPostListDto> sidebarPopularPosts = blogPostService.getMostPopularBlogPosts(
                PageRequest.of(0, 3));
        model.addAttribute("popularPosts", sidebarPopularPosts.getContent());

        return "client/blog/list";
    }

    @GetMapping("/{id}")
    public String viewBlogPost(@PathVariable Long id, Model model) {
        try {
            // Увеличиваем счетчик просмотров
            BlogPostDetailsDto blogPost = blogPostService.incrementViewCount(id);
            model.addAttribute("blogPost", blogPost);

            // Получаем связанные посты того же типа
            Page<BlogPostListDto> relatedPosts = blogPostService.getBlogPostsByType(
                    blogPost.getType().name(),
                    PageRequest.of(0, 3));

            model.addAttribute("relatedPosts", relatedPosts.getContent());

            return "client/blog/view";
        } catch (Exception e) {
            return "redirect:/blog";
        }
    }

    @GetMapping("/type/{type}")
    public String getBlogPostsByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            Model model) {

        return listBlogPosts(page, size, type, "createdAt", "desc", model);
    }






}