package com.example.olxwebscrabber.entity.enums;

public enum Type {
    FOR_RENT,
    FOR_SELL;

    public static Type ofPrice(Integer price) {
       return price < 100_000 ? FOR_RENT : FOR_SELL;
    }

}
