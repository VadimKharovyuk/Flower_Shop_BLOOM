package com.example.flowershoptr.controller;

import com.example.flowershoptr.model.SupportRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/contact")
@Controller
public class ContactController {

    @GetMapping
    public String contact() {


        return "contact";
    }


}
