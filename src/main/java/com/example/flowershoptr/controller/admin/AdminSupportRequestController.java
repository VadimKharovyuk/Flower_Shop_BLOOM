package com.example.flowershoptr.controller.admin;

import com.example.flowershoptr.model.SupportRequest;
import com.example.flowershoptr.service.SupportRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/support-requests")
@RequiredArgsConstructor
public class AdminSupportRequestController {

    private final SupportRequestService supportRequestService;


    @GetMapping
    public String viewSupportRequests(Model model) {
        model.addAttribute("requests", supportRequestService.findAll());
        return "admin/support/list";
    }

    // Детальный просмотр тикета
    @GetMapping("/{id}")
    public String viewSupportRequest(@PathVariable Long id, Model model) {
        SupportRequest request = supportRequestService.findById(id);
        if (request == null) {
            model.addAttribute("errorMessage", "Заявка не найдена");
            return "error";
        }
        model.addAttribute("request", request);
        return "admin/support/details";
    }

    // Удаление тикета
    @PostMapping("/{id}/delete")
    public String deleteSupportRequest(@PathVariable Long id) {
        supportRequestService.delete(id);
        return "redirect:/admin/support-requests";
    }
}