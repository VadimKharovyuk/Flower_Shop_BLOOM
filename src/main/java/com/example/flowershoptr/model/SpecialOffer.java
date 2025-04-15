package com.example.flowershoptr.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "special_offers")
public class SpecialOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Название акции (например "Сезонная коллекция", "Три по цене двух")

    @Column(columnDefinition = "TEXT")
    private String description; // Описание акции

    private String badgeText; // Текст бейджа (-20%, 2+1, -30%)

    @Column(columnDefinition = "TEXT")
    private String imageUrl; // URL изображения акции

    private BigDecimal oldPrice; // Старая цена (до скидки)

    private BigDecimal newPrice; // Новая цена (после скидки)

    private String specialPriceText; // Специальный текст для цены (например "Третий букет бесплатно")

    private LocalDateTime endDate; // Дата окончания акции

    private String timerDisplayType; // Тип отображения таймера ("days", "countdown")

    private boolean isFeatured; // Является ли акция особо выделенной

    private String buttonText; // Текст на кнопке

    private String buttonUrl; // URL для кнопки

    private boolean highlightButton; // Выделять ли кнопку

    @Column
    private Long photoId;

    @ManyToMany
    @JoinTable(
            name = "offer_flowers",
            joinColumns = @JoinColumn(name = "offer_id"),
            inverseJoinColumns = @JoinColumn(name = "flower_id")
    )
    private List<Flower> applicableFlowers; // Цветы, к которым применима акция

    private boolean isActive; // Активна ли акция в данный момент

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Метод для проверки, действует ли еще акция
    @Transient
    public boolean isStillValid() {
        return isActive && (endDate == null || endDate.isAfter(LocalDateTime.now()));
    }

    // Метод для получения оставшегося времени в днях
    @Transient
    public long getRemainingDays() {
        if (endDate == null) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), endDate).toDays();
    }

    // Метод для вычисления процента скидки
    @Transient
    public int getDiscountPercentage() {
        if (oldPrice == null || newPrice == null || oldPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        return oldPrice.subtract(newPrice)
                .multiply(new BigDecimal(100))
                .divide(oldPrice, 0, java.math.RoundingMode.HALF_UP)
                .intValue();
    }
}