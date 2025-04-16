package com.example.flowershoptr.controller.rest;

import com.example.flowershoptr.dto.SpecialOffer.SpecialOfferDetailsDTO;
import com.example.flowershoptr.dto.SpecialOfferCreateDTO;
import com.example.flowershoptr.dto.SpecialOfferListDTO;
import com.example.flowershoptr.service.SpecialOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/special-offers")
@RequiredArgsConstructor
public class AdminSpecialOfferController {

    private final SpecialOfferService specialOfferService;

    /**
     * Получение списка всех специальных предложений
     */
    @GetMapping
    public ResponseEntity<List<SpecialOfferListDTO>> getAllOffers() {
        List<SpecialOfferListDTO> offers = specialOfferService.getAllActiveOffers();
        return ResponseEntity.ok(offers);
    }

    /**
     * Получение детальной информации о конкретном предложении
     */
    @GetMapping("/{id}")
    public ResponseEntity<SpecialOfferDetailsDTO> getOfferDetails(@PathVariable Long id) {
        SpecialOfferDetailsDTO offer = specialOfferService.getOfferDetailsDto(id);
        return ResponseEntity.ok(offer);
    }

    /**
     * Создание нового специального предложения
     */
    @PostMapping
    public ResponseEntity<SpecialOfferDetailsDTO> createOffer(@RequestBody SpecialOfferCreateDTO createDto) {
        SpecialOfferDetailsDTO createdOffer = specialOfferService.createOffer(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOffer);
    }

    /**
     * Добавление цветов в специальное предложение
     */
    @PostMapping("/{id}/flowers")
    public ResponseEntity<Void> addFlowersToOffer(
            @PathVariable Long id,
            @RequestBody List<Long> flowerIds) {
        specialOfferService.addFlowersToOffer(id, flowerIds);
        return ResponseEntity.ok().build();
    }

    /**
     * Удаление цветов из специального предложения
     */
    @DeleteMapping("/{id}/flowers")
    public ResponseEntity<Void> removeFlowersFromOffer(
            @PathVariable Long id,
            @RequestBody List<Long> flowerIds) {
        specialOfferService.removeFlowersFromOffer(id, flowerIds);
        return ResponseEntity.ok().build();
    }

    /**
     * Обновление специального предложения
     */
    @PutMapping("/{id}")
    public ResponseEntity<SpecialOfferDetailsDTO> updateOffer(
            @PathVariable Long id,
            @RequestBody SpecialOfferCreateDTO updateDto) {
        // Предполагается, что метод updateOffer будет добавлен в сервис
        // Пока используем createOffer (нужно реализовать updateOffer)
        SpecialOfferDetailsDTO updatedOffer = specialOfferService.createOffer(updateDto);
        return ResponseEntity.ok(updatedOffer);
    }

    /**
     * Деактивация специального предложения
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateOffer(@PathVariable Long id) {
        // Предполагается, что метод deactivateOffer будет добавлен в сервис
        // specialOfferService.deactivateOffer(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Ручной запуск процесса деактивации просроченных предложений
     */
    @PostMapping("/deactivate-expired")
    public ResponseEntity<Void> deactivateExpiredOffers() {
        specialOfferService.deactivateExpiredOffers();
        return ResponseEntity.ok().build();
    }
}