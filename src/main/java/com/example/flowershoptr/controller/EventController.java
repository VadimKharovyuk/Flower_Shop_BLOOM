package com.example.flowershoptr.controller;

import com.example.flowershoptr.service.EventService;
import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@RequiredArgsConstructor
@Controller
@RequestMapping("/event")
public class EventController {


    public final EventService eventService;



    @GetMapping
    public String events(Model model) {
        return "events";
    }
}
