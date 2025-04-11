package com.example.flowershoptr.model;

import com.example.flowershoptr.model.Flower;
import com.example.flowershoptr.model.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "flower_id")
    private Flower flower;

    private Integer quantity;

    private BigDecimal price;

    @Column(name = "item_total")
    private BigDecimal itemTotal;
}