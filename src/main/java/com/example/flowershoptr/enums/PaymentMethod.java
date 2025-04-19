package com.example.flowershoptr.enums;

import lombok.Getter;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    LIQPAY("Приват Банк"),
    CASH_ON_DELIVERY("Наличными при доставке"),
    BANK_TRANSFER("Банковский перевод"),
    ELECTRONIC_WALLET("Электронный кошелёк"),
    MONOBANK("Monobank"),
    PAYPAL("PayPal"),
    CREDIT_CARD("Кредитная карта");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }
}