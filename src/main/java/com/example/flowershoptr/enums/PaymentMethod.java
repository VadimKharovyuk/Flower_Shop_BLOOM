package com.example.flowershoptr.enums;

import lombok.Getter;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CREDIT_CARD("Кредитная карта"),
    DEBIT_CARD("Дебетовая карта"),
    CASH_ON_DELIVERY("Наличными при доставке"),
    BANK_TRANSFER("Банковский перевод"),
    ELECTRONIC_WALLET("Электронный кошелёк");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }
}