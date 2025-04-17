package com.example.flowershoptr.controller.rest;

import com.example.flowershoptr.dto.Subscrip.SubscriptionDto;
import com.example.flowershoptr.model.Subscription;
import com.example.flowershoptr.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {

    private final SubscriptionService subscriptionService;


    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@Valid @RequestBody SubscriptionDto request) {
        try {
            Subscription subscription = subscriptionService.subscribe(request);

            if (subscription != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Не удалось создать подписку");
            }
        } catch (Exception e) {
            log.error("Ошибка при создании подписки: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при создании подписки: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Subscription>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Subscription>> getActiveSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getActiveSubscriptions());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getSubscriberCount() {
        return ResponseEntity.ok(subscriptionService.getActiveSubscriberCount());
    }




    @PostMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribe(@Valid @RequestBody SubscriptionDto request) {
        boolean unsubscribed = subscriptionService.unsubscribe(request.getEmail());

        if (unsubscribed) {
            return ResponseEntity.ok("Вы успешно отписались от рассылки");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Подписка с указанным email не найдена");
        }
    }
}