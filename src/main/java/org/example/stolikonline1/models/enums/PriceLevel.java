package org.example.stolikonline1.models.enums;

public enum PriceLevel {
    LOW(1), MEDIUM(2), HIGH(3);

    private int value;

    PriceLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}