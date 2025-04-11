package com.example.flowershoptr.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING("Ожидается"),
    PAID("Оплачен"),
    FAILED("Ошибка оплаты"),
    REFUNDED("Возвращен");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

}