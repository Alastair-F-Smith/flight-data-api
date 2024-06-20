package com.example.afs.flightdataapi.model.dto;

import com.example.afs.flightdataapi.model.entities.AircraftsData;

public record AircraftDto(String aircraftCode,
                          String model,
                          int range) {

    public static AircraftDto from(AircraftsData entity) {
        return new AircraftDto(entity.getAircraftCode(), entity.getModel().en(), entity.getRange());
    }
}
