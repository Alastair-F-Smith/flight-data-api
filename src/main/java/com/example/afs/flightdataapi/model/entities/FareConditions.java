package com.example.afs.flightdataapi.model.entities;

public enum FareConditions {
    BUSINESS("Business"), COMFORT("Comfort"), ECONOMY("Economy");

    private final String value;

    FareConditions(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FareConditions from(String dbvalue) {
        return switch(dbvalue) {
            case "Business" -> FareConditions.BUSINESS;
            case "Comfort" -> FareConditions.COMFORT;
            case "Economy" -> FareConditions.ECONOMY;
            default -> throw new IllegalArgumentException("Invalid fare conditions: " + dbvalue);
        };
    }
}
