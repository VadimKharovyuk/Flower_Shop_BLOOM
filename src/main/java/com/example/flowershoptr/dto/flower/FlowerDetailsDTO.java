//package com.example.flowershoptr.dto.flower;
//
//import lombok.Data;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Data
//public class FlowerDetailsDTO {
//    private Long id;
//    private CategoryDTO category; // Вложенный DTO для категории
//    private String name;
//    private String fullDescription;
//    private String shortDescription;
//    private String previewImageUrl;
//    private Long photoId;
//    private Integer count;
//    private BigDecimal price;
//    private boolean isActive;
//    private boolean hasDeliveryToday;
//    private double weight;
//    private Integer reviewCount;
//    private Double averageRating;
//    private LocalDateTime createdAt;
//
//    // Вложенный DTO для категории
//    @Data
//    public static class CategoryDTO {
//        private Long id;
//        private String name;
//        // Другие необходимые поля категории
//    }
//}
package com.example.flowershoptr.dto.flower;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
public class FlowerDetailsDTO {
    private Long id;
    private CategoryDTO category; // Вложенный DTO для категории
    private String name;
    private String fullDescription;
    private String shortDescription;
    private String previewImageUrl;
    private Long photoId;
    private Integer count;
    private BigDecimal price;
    private boolean isActive;
    private boolean hasDeliveryToday;
    private double weight;
    private Integer reviewCount;
    private Double averageRating;
    private LocalDateTime createdAt;


    // Вложенный DTO для категории
    @Data
    public static class CategoryDTO {
        private Long id;
        private String name;
        // Другие необходимые поля категории
    }
}