package com.example.flowershoptr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
@Getter
public enum CustomBouquetRequestStatus {
    NEW("Новая заявка"),
    PROCESSING("В обработке"),
    DESIGN_STAGE("Разработка дизайна"),
    AWAITING_APPROVAL("Ожидает подтверждения клиента"),
    CONFIRMED("Подтверждено клиентом"),
    IN_PRODUCTION("В производстве"),
    READY("Готов к выдаче/доставке"),
    DELIVERED("Доставлен"),
    COMPLETED("Завершено"),
    CANCELLED("Отменено");

    private final String displayName;

    @Override
    public String toString() {
        return displayName;
    }
}