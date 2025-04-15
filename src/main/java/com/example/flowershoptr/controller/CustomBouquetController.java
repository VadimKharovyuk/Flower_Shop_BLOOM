package com.example.flowershoptr.controller;

import com.example.flowershoptr.dto.Custom.CustomBouquetRequestDTO;
import com.example.flowershoptr.enums.CustomBouquetRequestStatus;
import com.example.flowershoptr.exception.ResourceNotFoundException;
import com.example.flowershoptr.model.CustomBouquetRequest;
import com.example.flowershoptr.service.CustomBouquetService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/custom-bouquet")
public class CustomBouquetController {

    private final CustomBouquetService service;

    @Autowired
    public CustomBouquetController(CustomBouquetService service) {
        this.service = service;
    }

    /**
     * Создание новой заявки на индивидуальный букет
     * @param requestDTO Данные заявки
     * @return Созданная заявка
     */
    @PostMapping("/request")
    public ResponseEntity<CustomBouquetRequest> createRequest(@RequestBody CustomBouquetRequestDTO requestDTO) {
        CustomBouquetRequest savedRequest = service.saveRequest(requestDTO);
        return new ResponseEntity<>(savedRequest, HttpStatus.CREATED);
    }

    /**
     * Получение всех заявок (для админа)
     * @return Список всех заявок
     */
    @GetMapping("/admin/requests")
    public ResponseEntity<List<CustomBouquetRequest>> getAllRequests() {
        return ResponseEntity.ok(service.getAllRequests());
    }

    /**
     * Получение заявок по статусу (для админа)
     * @param status Статус заявки
     * @return Список заявок с указанным статусом
     */
    @GetMapping("/admin/requests/status/{status}")
    public ResponseEntity<List<CustomBouquetRequest>> getRequestsByStatus(@PathVariable String status) {
        try {
            CustomBouquetRequestStatus requestStatus =
                    CustomBouquetRequestStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(service.getRequestsByStatus(requestStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Получение заявки по ID (для админа)
     * @param id ID заявки
     * @return Заявка с указанным ID или 404
     */
    @GetMapping("/admin/requests/{id}")
    public ResponseEntity<CustomBouquetRequest> getRequestById(@PathVariable Long id) {
        return service.getRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Обновление статуса заявки (для админа)
     * @param id ID заявки
     * @param status Новый статус
     * @param adminComment Комментарий администратора (опционально)
     * @return Обновленная заявка
     */
    @PutMapping("/admin/requests/{id}/status")
    public ResponseEntity<?> updateRequestStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String adminComment) {

        try {
            CustomBouquetRequestStatus requestStatus =
                    CustomBouquetRequestStatus.valueOf(status.toUpperCase());
            CustomBouquetRequest updatedRequest = service.updateStatus(id, requestStatus, adminComment);
            return ResponseEntity.ok(updatedRequest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Неверный статус: " + status);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Публичный API для проверки статуса заявки клиентом
     * @param id ID заявки
     * @param phone Телефон для верификации
     * @return Статус заявки
     */
    @GetMapping("/request/status")
    public ResponseEntity<?> checkRequestStatus(
            @RequestParam Long id,
            @RequestParam String phone) {

        Optional<CustomBouquetRequest> requestOpt = service.getRequestById(id);

        if (requestOpt.isPresent() && requestOpt.get().getPhone().equals(phone)) {
            CustomBouquetRequest req = requestOpt.get();
            return ResponseEntity.ok(new RequestStatusResponse(
                    req.getId(),
                    req.getStatus(),
                    req.getStatus().getDisplayName()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Заявка не найдена или указан неверный телефон");
        }
    }



    /**
     * Вспомогательный класс для возврата статуса клиенту
     */
    @Getter
    private static class RequestStatusResponse {
        private final Long id;
        private final CustomBouquetRequestStatus status;
        private final String statusDisplayName;

        public RequestStatusResponse(Long id, CustomBouquetRequestStatus status, String statusDisplayName) {
            this.id = id;
            this.status = status;
            this.statusDisplayName = statusDisplayName;
        }

    }
}