package com.example.flowershoptr.enums;

import lombok.Getter;

import lombok.Getter;

@Getter
public enum BookingStatus {
    PENDING("Ожидает подтверждения"),
    CONFIRMED("Подтверждено"),
    CANCELLED("Отменено"),
    ATTENDED("Посещение состоялось"),
    NO_SHOW("Не явился");

    private final String description;

    BookingStatus(String description) {
        this.description = description;
    }
}