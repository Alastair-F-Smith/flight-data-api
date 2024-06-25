package com.example.afs.flightdataapi.controllers.documentation;

public enum RequestTypes {
    AIRCRAFT(ExampleData.AIRCRAFT_VALID, ExampleData.AIRCRAFT_INVALID),
    SEAT(ExampleData.SEAT_VALID, ExampleData.SEAT_INVALID),
    AIRPORT(ExampleData.AIRPORT_VALID, ExampleData.AIRPORT_INVALID),
    PERSONAL_DETAILS(ExampleData.PERSONAL_DETAILS_VALID, ExampleData.PERSONAL_DETAILS_INVALID);

    private final String valid;
    private final String invalid;

    RequestTypes(String valid, String invalid) {
        this.valid = valid;
        this.invalid = invalid;
    }

    public String getValid() {
        return valid;
    }

    public String getInvalid() {
        return invalid;
    }
}
