package com.example.flowershoptr.controller;

import com.example.flowershoptr.dto.flower.FlowerSearchDTO;
import com.example.flowershoptr.service.FlowerService;
import com.example.flowershoptr.util.PaginationUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class Home {



    @GetMapping
    public String homePage(HttpSession session, Model model) {
       String s =session.getId();
        System.out.println(s);


        return "home";
    }




    @GetMapping("/test")
    public String test(Model model) {
    return "test";
    }
}
