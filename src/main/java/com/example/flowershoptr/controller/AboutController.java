package com.example.flowershoptr.controller;

import com.example.flowershoptr.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/about")
public class AboutController {


    @GetMapping
    public String about() {
        return "about";
    }

}
