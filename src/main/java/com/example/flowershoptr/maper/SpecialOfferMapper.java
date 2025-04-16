package com.example.flowershoptr.maper;

import com.example.flowershoptr.dto.SpecialOffer.FlowerShortDTO;
import com.example.flowershoptr.dto.SpecialOffer.SpecialOfferDetailsDTO;
import com.example.flowershoptr.dto.SpecialOfferCreateDTO;
import com.example.flowershoptr.dto.SpecialOfferListDTO;
import com.example.flowershoptr.model.Flower;
import com.example.flowershoptr.model.SpecialOffer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SpecialOfferMapper {

    /**
     * Преобразует DTO создания в сущность SpecialOffer
     */
    public SpecialOffer toEntity(SpecialOfferCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        SpecialOffer offer = new SpecialOffer();
        offer.setName(dto.getName());
        offer.setDescription(dto.getDescription());
        offer.setBadgeText(dto.getBadgeText());
        offer.setImageUrl(dto.getImageUrl());
        offer.setPhotoId(dto.getPhotoId());
        offer.setOldPrice(dto.getOldPrice());
        offer.setNewPrice(dto.getNewPrice());
        offer.setSpecialPriceText(dto.getSpecialPriceText());
        offer.setEndDate(dto.getEndDate());
        offer.setTimerDisplayType(dto.getTimerDisplayType());
        offer.setFeatured(dto.isFeatured());
        offer.setButtonText(dto.getButtonText());
        offer.setButtonUrl(dto.getButtonUrl());
        offer.setHighlightButton(dto.isHighlightButton());
        offer.setActive(dto.isActive());

        return offer;
    }

    /**
     * Преобразует сущность SpecialOffer в DTO для списка (без вычисляемых полей)
     */
    public SpecialOfferListDTO toListDto(SpecialOffer offer) {
        if (offer == null) {
            return null;
        }

        SpecialOfferListDTO dto = new SpecialOfferListDTO();
        dto.setId(offer.getId());
        dto.setName(offer.getName());
        dto.setBadgeText(offer.getBadgeText());
        dto.setImageUrl(offer.getImageUrl());
        dto.setOldPrice(offer.getOldPrice());
        dto.setNewPrice(offer.getNewPrice());
        dto.setSpecialPriceText(offer.getSpecialPriceText());
        dto.setEndDate(offer.getEndDate());
        dto.setTimerDisplayType(offer.getTimerDisplayType());
        dto.setFeatured(offer.isFeatured());
        dto.setButtonText(offer.getButtonText());
        dto.setButtonUrl(offer.getButtonUrl());
        dto.setHighlightButton(offer.isHighlightButton());
        dto.setFlowerCount(offer.getApplicableFlowers().size());

        return dto;
    }

    /**
     * Преобразует список сущностей SpecialOffer в список DTO для списка (без вычисляемых полей)
     */
    public List<SpecialOfferListDTO> toListDto(List<SpecialOffer> offers) {
        return offers.stream()
                .map(this::toListDto)
                .collect(Collectors.toList());
    }

    /**
     * Преобразует сущность SpecialOffer в DTO с подробной информацией (без вычисляемых полей)
     */
    public SpecialOfferDetailsDTO toDetailsDto(SpecialOffer offer) {
        if (offer == null) {
            return null;
        }

        SpecialOfferDetailsDTO dto = new SpecialOfferDetailsDTO();
        dto.setId(offer.getId());
        dto.setName(offer.getName());
        dto.setDescription(offer.getDescription());
        dto.setBadgeText(offer.getBadgeText());
        dto.setImageUrl(offer.getImageUrl());
        dto.setOldPrice(offer.getOldPrice());
        dto.setNewPrice(offer.getNewPrice());
        dto.setSpecialPriceText(offer.getSpecialPriceText());
        dto.setEndDate(offer.getEndDate());
        dto.setTimerDisplayType(offer.getTimerDisplayType());
        dto.setFeatured(offer.isFeatured());
        dto.setButtonText(offer.getButtonText());
        dto.setButtonUrl(offer.getButtonUrl());
        dto.setHighlightButton(offer.isHighlightButton());
        dto.setCreatedAt(offer.getCreatedAt());

        return dto;
    }

    /**
     * Преобразует сущность Flower в краткое DTO (без вычисляемых полей)
     */
    public FlowerShortDTO toFlowerShortDto(Flower flower) {
        if (flower == null) {
            return null;
        }

        FlowerShortDTO dto = new FlowerShortDTO();
        dto.setId(flower.getId());
        dto.setName(flower.getName());
        dto.setPreviewImageUrl(flower.getPreviewImageUrl());
        dto.setRegularPrice(flower.getPrice());

        return dto;
    }

    /**
     * Преобразует список сущностей Flower в список кратких DTO
     */
    public List<FlowerShortDTO> toFlowerShortDtoList(List<Flower> flowers) {
        return flowers.stream()
                .map(this::toFlowerShortDto)
                .collect(Collectors.toList());
    }
}