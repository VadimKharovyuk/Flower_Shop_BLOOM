package com.example.flowershoptr.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboard {


    @GetMapping
    public String adminDashboard() {
        return "admin/dashboard";
    }
}
