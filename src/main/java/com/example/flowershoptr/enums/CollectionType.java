package com.example.flowershoptr.enums;

import lombok.Getter;
@Getter
public enum CollectionType {
    WEDDING("Свадебная коллекция"),
    SEASONAL("Сезонная коллекция"),
    POPULAR("Популярные букеты"),
    BIRTHDAY("Букеты на день рождения"),
    CORPORATE("Корпоративные букеты"),
    ANNIVERSARY("Юбилейная коллекция"),
    ROMANTIC("Романтические букеты"),
    GRADUATION("Букеты на выпускной"),
    VALENTINES_DAY("Букеты ко дню святого Валентина"),
    CHRISTMAS("Рождественская коллекция"),
    SPRING("Весенняя коллекция"),
    SUMMER("Летняя коллекция"),
    AUTUMN("Осенняя коллекция"),
    WINTER("Зимняя коллекция");

    private final String displayName;

    CollectionType(String displayName) {
        this.displayName = displayName;
    }
}
