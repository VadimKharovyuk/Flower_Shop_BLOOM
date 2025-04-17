package com.example.flowershoptr.controller.admin;

import com.example.flowershoptr.model.InstagramPost;
import com.example.flowershoptr.service.InstagramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/instagram")
@RequiredArgsConstructor
public class AdminInstagramController {
    private final InstagramService instagramService;

    @GetMapping
    public String showInstagramPosts(Model model) {
        model.addAttribute("posts", instagramService.getActivePosts());
        model.addAttribute("newPost", new InstagramPost());
        return "admin/instagram/index";
    }

    @PostMapping("/save")
    public String savePost(@ModelAttribute InstagramPost post, RedirectAttributes redirectAttributes) {
        instagramService.savePost(post);
        redirectAttributes.addFlashAttribute("successMessage", "Пост Instagram успешно сохранен");
        return "redirect:/admin/instagram";
    }

    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        instagramService.deletePost(id);
        redirectAttributes.addFlashAttribute("successMessage", "Пост Instagram успешно удален");
        return "redirect:/admin/instagram";
    }
}