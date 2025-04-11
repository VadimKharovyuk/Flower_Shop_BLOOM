package com.example.flowershoptr.enums;

import lombok.Getter;

@Getter
public enum Season {
    SPRING("Весна"),
    SUMMER("Лето"),
    AUTUMN("Осень"),
    WINTER("Зима");

    private final String displayName;

    Season(String displayName) {
        this.displayName = displayName;
    }
}