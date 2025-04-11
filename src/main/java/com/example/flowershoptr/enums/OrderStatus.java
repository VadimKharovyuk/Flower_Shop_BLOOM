package com.example.flowershoptr.enums;

import lombok.Getter;


@Getter
public enum OrderStatus {
    NEW("Новый"),
    PROCESSING("В обработке"),
    COMPLETED("Выполнен"),
    CANCELLED("Отменен");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }


}