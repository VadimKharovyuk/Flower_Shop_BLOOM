package com.example.flowershoptr.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "cart_items")
public class CartItem  implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "flower_id")
    private Flower flower;

    private Integer quantity;

    private BigDecimal price;// Обычная цена без скидки

    @Column(name = "item_total")
    private BigDecimal itemTotal;

    // Новые поля для работы со скидками
    private BigDecimal DiscountPrice;
    private boolean hasDiscount;      // Флаг наличия скидки
    private LocalDateTime discountExpiryDate;


}