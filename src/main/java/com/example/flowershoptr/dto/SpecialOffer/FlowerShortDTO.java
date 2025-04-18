package com.example.flowershoptr.dto.SpecialOffer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowerShortDTO {
    private Long id;
    private String name;
    private String previewImageUrl;
    private BigDecimal regularPrice;
    private BigDecimal discountedPrice;
}