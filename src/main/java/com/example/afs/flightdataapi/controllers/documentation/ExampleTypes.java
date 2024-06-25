package com.example.afs.flightdataapi.controllers.documentation;

public enum ExampleTypes {
    VALID("Valid data"), INVALID("Invalid data");

    public final String description;

    ExampleTypes(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
