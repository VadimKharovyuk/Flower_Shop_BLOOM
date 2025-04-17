package com.example.flowershoptr.enums;

import lombok.Getter;

@Getter
public enum BlogPostType {
    INTERESTING_FACTS("Интересные факты"),
    USEFUL_TIPS("Полезные советы"),
    FLOWER_CARE("Уход за цветами"),
    HOLIDAY_GUIDES("Гид по праздникам"),
    BOUQUET_IDEAS("Идеи для букетов"),
    SHOP_NEWS("Новости магазина");

    private final String displayName;

    BlogPostType(String displayName) {
        this.displayName = displayName;
    }

}