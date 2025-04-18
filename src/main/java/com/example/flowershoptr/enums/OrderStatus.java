package com.example.flowershoptr.enums;

import lombok.Getter;


import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("Ожидает оплаты"),
    PAID("Оплачен"),
    PROCESSING("В обработке"),
    SHIPPED("Отправлен"),
    DELIVERED("Доставлен"),
    COMPLETED("Завершен"),
    CANCELED("Отменен"),
    REFUNDED("Возвращен");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }


}