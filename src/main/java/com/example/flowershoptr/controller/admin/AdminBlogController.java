package com.example.flowershoptr.controller.admin;

import com.example.flowershoptr.dto.BlogPost.BlogPostCreateUpdateDto;
import com.example.flowershoptr.dto.BlogPost.BlogPostDetailsDto;
import com.example.flowershoptr.dto.BlogPost.BlogPostListDto;
import com.example.flowershoptr.enums.BlogPostType;
import com.example.flowershoptr.service.BlogPostService;

import com.example.flowershoptr.service.serviceImpl.BlogPostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/admin/blog")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
public class AdminBlogController {

    private final BlogPostServiceImpl blogPostService;

    @GetMapping
    public String listBlogPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            Model model) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Page<BlogPostListDto> blogPosts = blogPostService.getBlogPosts(
                PageRequest.of(page, size, sort));

        model.addAttribute("blogPosts", blogPosts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", blogPosts.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);

        return "admin/blog/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("blogPost", BlogPostCreateUpdateDto.builder().build());
        model.addAttribute("postTypes", BlogPostType.values());
        return "admin/blog/create";
    }

    @PostMapping("/create")
    public String createBlogPost(
            @ModelAttribute BlogPostCreateUpdateDto blogPost,
            @RequestParam(required = false) MultipartFile image,
            RedirectAttributes redirectAttributes) {

        try {
            BlogPostDetailsDto created = blogPostService.createBlogPostWithImage(blogPost, image);
            redirectAttributes.addFlashAttribute("success", "Пост успешно создан!");
            return "redirect:/admin/blog";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при загрузке изображения: " + e.getMessage());
            return "redirect:/admin/blog/create";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании поста: " + e.getMessage());
            return "redirect:/admin/blog/create";
        }
    }

    @GetMapping("/{id}")
    public String viewBlogPost(@PathVariable Long id, Model model) {
        try {
            BlogPostDetailsDto blogPost = blogPostService.getBlogPostById(id);
            model.addAttribute("blogPost", blogPost);
            return "admin/blog/view";
        } catch (Exception e) {
            return "redirect:/admin/blog";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            BlogPostDetailsDto blogPost = blogPostService.getBlogPostById(id);

            // Конвертируем из DetailedDTO в CreateUpdateDTO
            BlogPostCreateUpdateDto editDto = BlogPostCreateUpdateDto.builder()
                    .title(blogPost.getTitle())
                    .shortDescription(blogPost.getShortDescription())
                    .fullContent(blogPost.getFullContent())
                    .type(blogPost.getType())
                    .previewImageUrl(blogPost.getPreviewImageUrl())
                    .build();

            model.addAttribute("blogPost", editDto);
            model.addAttribute("blogPostId", id);
            model.addAttribute("postTypes", BlogPostType.values());
            return "admin/blog/edit";
        } catch (Exception e) {
            return "redirect:/admin/blog";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateBlogPost(
            @PathVariable Long id,
            @ModelAttribute BlogPostCreateUpdateDto blogPost,
            @RequestParam(required = false) MultipartFile image,
            RedirectAttributes redirectAttributes) {

        try {
            BlogPostDetailsDto updated = blogPostService.updateBlogPostWithImage(id, blogPost, image);
            redirectAttributes.addFlashAttribute("success", "Пост успешно обновлен!");
            return "redirect:/admin/blog";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при загрузке изображения: " + e.getMessage());
            return "redirect:/admin/blog/" + id + "/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении поста: " + e.getMessage());
            return "redirect:/admin/blog/" + id + "/edit";
        }
    }

    @GetMapping("/{id}/delete")
    public String confirmDeleteBlogPost(@PathVariable Long id, Model model) {
        try {
            BlogPostDetailsDto blogPost = blogPostService.getBlogPostById(id);
            model.addAttribute("blogPost", blogPost);
            return "admin/blog/delete-confirm";
        } catch (Exception e) {
            return "redirect:/admin/blog";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteBlogPost(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            blogPostService.deleteBlogPost(id);
            redirectAttributes.addFlashAttribute("success", "Пост успешно удален!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении поста: " + e.getMessage());
        }

        return "redirect:/admin/blog";
    }
}