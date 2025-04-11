package com.example.flowershoptr.enums;

import lombok.Getter;

@Getter
public enum EventType {
    MASTERCLASS("Мастер-класс"),
    WORKSHOP("Воркшоп"),
    MEETING("Встреча"),
    PRESENTATION("Презентация"),
    EXHIBITION("Выставка"),
    FLORIST_COMPETITION("Конкурс флористов"),
    FLOWER_SHOW("Цветочная выставка"),
    SEASONAL_EVENT("Сезонное мероприятие"),
    CHARITY_EVENT("Благотворительное мероприятие"),
    CUSTOMER_APPRECIATION("Мероприятие для клиентов"),
    FLORAL_DESIGN_EXPO("Выставка флористического дизайна"),
    HOLIDAY_EVENT("Праздничное мероприятие");


    private final String displayName;

    EventType(String displayName) {
        this.displayName = displayName;
    }
}