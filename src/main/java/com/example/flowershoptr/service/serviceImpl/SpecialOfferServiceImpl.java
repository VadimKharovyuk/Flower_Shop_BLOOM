package com.example.flowershoptr.service.serviceImpl;

import com.example.flowershoptr.dto.SpecialOffer.FlowerShortDTO;
import com.example.flowershoptr.dto.SpecialOffer.SpecialOfferCreateDTO;
import com.example.flowershoptr.dto.SpecialOffer.SpecialOfferDetailsDTO;

import com.example.flowershoptr.dto.SpecialOfferListDTO;
import com.example.flowershoptr.maper.SpecialOfferMapper;
import com.example.flowershoptr.model.Flower;
import com.example.flowershoptr.model.SpecialOffer;
import com.example.flowershoptr.repository.FlowerRepository;

import com.example.flowershoptr.repository.SpecialOfferRepository;
import com.example.flowershoptr.service.SpecialOfferService;
import com.example.flowershoptr.service.StorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Service
@Slf4j
public class SpecialOfferServiceImpl implements SpecialOfferService {
    private final SpecialOfferRepository specialOfferRepository;
    private final FlowerRepository flowerRepository;
    private final SpecialOfferMapper mapper;
    private final StorageService storageService;

    @Override
    @Transactional
    public SpecialOfferDetailsDTO createOffer(SpecialOfferCreateDTO createDto, MultipartFile imageFile) {
        // Загружаем изображение, если оно предоставлено
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                StorageService.StorageResult uploadResult = storageService.uploadImage(imageFile);
                createDto.setImageUrl(uploadResult.getUrl());
                createDto.setPublicId(uploadResult.getImageId());
            } catch (IOException e) {
                log.error("Ошибка при загрузке изображения для акции: {}", e.getMessage());
            }
        }

        // Преобразование DTO в сущность (теперь с обновленными url и publicId)
        SpecialOffer offer = mapper.toEntity(createDto);

        // Сохранение сущности
        SpecialOffer savedOffer = specialOfferRepository.save(offer);

        // Если переданы ID цветов, связываем их с предложением
        if (createDto.getFlowerIds() != null && !createDto.getFlowerIds().isEmpty()) {
            List<Flower> flowers = flowerRepository.findAllById(createDto.getFlowerIds());
            savedOffer.setApplicableFlowers(flowers);
            savedOffer = specialOfferRepository.save(savedOffer);
        }

        // Возврат с деталями, включая вычисляемые поля
        return getOfferDetailsDto(savedOffer.getId());
    }

    @Override
    public List<SpecialOfferListDTO> getAllActiveOffers() {
        List<SpecialOffer> activeOffers = specialOfferRepository.findByActiveTrue();

        // Фильтрация по дате окончания (если указана)
        List<SpecialOffer> validOffers = activeOffers.stream()
                .filter(this::isOfferStillValid)
                .toList();

        // Преобразование сущностей в DTO с добавлением вычисляемых полей
        return validOffers.stream()
                .map(offer -> {
                    SpecialOfferListDTO dto = mapper.toListDto(offer);
                    enrichListDto(dto, offer);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public SpecialOfferDetailsDTO getOfferDetailsDto(Long offerId) {
        SpecialOffer offer = specialOfferRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Специальное предложение не найдено"));

        // Базовое преобразование
        SpecialOfferDetailsDTO dto = mapper.toDetailsDto(offer);

        // Добавление вычисляемых полей
        enrichDetailsDto(dto, offer);

        return dto;
    }

    @Override
    @Transactional
    public void addFlowersToOffer(Long offerId, List<Long> flowerIds) {
        SpecialOffer offer = specialOfferRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Специальное предложение не найдено"));

        List<Flower> flowersToAdd = flowerRepository.findAllById(flowerIds);
        offer.getApplicableFlowers().addAll(flowersToAdd);

        specialOfferRepository.save(offer);
    }

    @Override
    @Transactional
    public void removeFlowersFromOffer(Long offerId, List<Long> flowerIds) {
        SpecialOffer offer = specialOfferRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Специальное предложение не найдено"));

        offer.setApplicableFlowers(
                offer.getApplicableFlowers().stream()
                        .filter(flower -> !flowerIds.contains(flower.getId()))
                        .collect(Collectors.toList())
        );

        specialOfferRepository.save(offer);
    }

    @Override
    @Transactional
    public void deactivateExpiredOffers() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Проверка на истекшие скидки начата. Текущее время: {}", now);

        List<SpecialOffer> activeOffers = specialOfferRepository.findByActiveTrue();
        log.info("Найдено активных предложений: {}", activeOffers.size());

        List<SpecialOffer> expiredOffers = new ArrayList<>();

        for (SpecialOffer offer : activeOffers) {
            LocalDateTime endDate = offer.getEndDate();
            log.debug("Проверка предложения ID={}, дата окончания: {}", offer.getId(), endDate);

            if (endDate != null && endDate.isBefore(now)) {
                log.info("Предложение ID={} истекло. Деактивируем...", offer.getId());
                offer.setActive(false);
                expiredOffers.add(offer);
            }
        }

        if (!expiredOffers.isEmpty()) {
            specialOfferRepository.saveAll(expiredOffers);
            log.info("Сохранены деактивированные предложения: {}", expiredOffers.stream()
                    .map(SpecialOffer::getId)
                    .toList());
        } else {
            log.info("Истекших предложений не найдено.");
        }
    }

    @Override
    public BigDecimal getDiscountedPrice(Flower flower) {
        Optional<SpecialOffer> bestOffer = getBestOffer(flower);
        if (bestOffer.isPresent() && bestOffer.get().getNewPrice() != null) {
            return bestOffer.get().getNewPrice();
        }
        return flower.getPrice();
    }

    @Override
    public boolean hasActiveDiscount(Flower flower) {
        return flower.getSpecialOffers().stream()
                .anyMatch(this::isOfferStillValid);
    }

    @Override
    public Optional<SpecialOffer> getBestOffer(Flower flower) {
        return flower.getSpecialOffers().stream()
                .filter(this::isOfferStillValid)
                .max((o1, o2) -> getDiscountPercentage(o1) - getDiscountPercentage(o2));
    }

    @Override
    public boolean isOfferStillValid(SpecialOffer offer) {
        return offer.isActive() && (offer.getEndDate() == null ||
                offer.getEndDate().isAfter(LocalDateTime.now()));
    }

    @Override
    public long getRemainingDays(SpecialOffer offer) {
        if (offer.getEndDate() == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDateTime.now(), offer.getEndDate());
    }

    @Override
    public int getDiscountPercentage(SpecialOffer offer) {
        if (offer.getOldPrice() == null || offer.getNewPrice() == null ||
                offer.getOldPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        return offer.getOldPrice().subtract(offer.getNewPrice())
                .multiply(new BigDecimal(100))
                .divide(offer.getOldPrice(), 0, RoundingMode.HALF_UP)
                .intValue();
    }

    // Вспомогательные методы для обогащения DTO вычисляемыми полями

    /**
     * Дополняет DTO списка вычисляемыми полями
     */
    private void enrichListDto(SpecialOfferListDTO dto, SpecialOffer offer) {
        dto.setRemainingDays(getRemainingDays(offer));
        dto.setDiscountPercentage(getDiscountPercentage(offer));
    }

    /**
     * Дополняет DTO деталей вычисляемыми полями
     */
    private void enrichDetailsDto(SpecialOfferDetailsDTO dto, SpecialOffer offer) {
        // Основные вычисляемые поля
        dto.setRemainingDays(getRemainingDays(offer));
        dto.setDiscountPercentage(getDiscountPercentage(offer));

        // Расчет суммы экономии
        if (offer.getOldPrice() != null && offer.getNewPrice() != null) {
            dto.setSavedAmount(offer.getOldPrice().subtract(offer.getNewPrice()).intValue());
        } else {
            dto.setSavedAmount(0);
        }

        // Преобразование и обогащение информации о цветах
        List<FlowerShortDTO> flowerDtos = offer.getApplicableFlowers().stream()
                .map(flower -> {
                    FlowerShortDTO flowerDto = mapper.toFlowerShortDto(flower);
                    flowerDto.setDiscountedPrice(getDiscountedPrice(flower));
                    return flowerDto;
                })
                .collect(Collectors.toList());

        dto.setApplicableFlowers(flowerDtos);
    }


    public SpecialOfferCreateDTO getOfferForEdit(Long id) {
        SpecialOfferDetailsDTO detailsDto = getOfferDetailsDto(id);
        return mapper.toCreateDto(detailsDto);
    }

    @Transactional
    public SpecialOfferDetailsDTO updateOffer(Long id, SpecialOfferCreateDTO offerDto) {
        SpecialOffer existingOffer = specialOfferRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Специальное предложение с ID " + id + " не найдено"));

        // Обновление полей сущности из DTO
        mapper.updateEntityFromDto(offerDto, existingOffer);

        // Дополнительная бизнес-логика, если нужна
        // Например, обработка связей с цветами

        // Сохранение обновленной сущности
        SpecialOffer updatedOffer = specialOfferRepository.save(existingOffer);

        // Преобразование в DTO и возврат
        return getOfferDetailsDto(updatedOffer.getId());
    }
}