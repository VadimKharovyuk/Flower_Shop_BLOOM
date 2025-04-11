package com.example.flowershoptr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Home {


@GetMapping
    public String homePage(Model model) {
        return "home";
    }
    @GetMapping("/test")
    public String test(Model model) {
    return "test";
    }
}
