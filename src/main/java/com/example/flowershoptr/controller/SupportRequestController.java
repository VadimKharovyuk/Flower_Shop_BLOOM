package com.example.flowershoptr.controller;

import com.example.flowershoptr.model.SupportRequest;
import com.example.flowershoptr.service.SupportRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/support")
@RequiredArgsConstructor
public class SupportRequestController {

    private final SupportRequestService supportRequestService;



    @PostMapping
    public ResponseEntity<String> createSupportRequest(@RequestBody SupportRequest supportRequest) {
        supportRequestService.save(supportRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Заявка успешно отправлена. Мы свяжемся с вами в ближайшее время.");
    }


}
