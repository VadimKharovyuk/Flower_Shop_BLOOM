package com.example.flowershoptr.dto.flower;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Data
public class FlowerSearchDTO {
    private Long id;
    private String name;
    private String previewImageUrl;
    private BigDecimal price;
    private Integer rating;
    private Double averageRating;


}