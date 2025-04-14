package com.example.flowershoptr.dto.flower;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class PopularFlowerDto {
    private Long id;
    private String name;
    private String previewImageUrl;
    private BigDecimal price;
    private String categoryName;
    private Integer popularityCount;
    private Integer favoritesCount;


}