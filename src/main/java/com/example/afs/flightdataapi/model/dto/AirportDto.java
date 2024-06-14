package com.example.afs.flightdataapi.model.dto;

import com.example.afs.flightdataapi.model.entities.Airport;

import java.util.TimeZone;

public record AirportDto(String airportCode, String name, String city, Point coordinates, TimeZone timeZone) {

    public static AirportDto from(Airport airport) {
        return new AirportDto(airport.getAirportCode(),
                              airport.getAirportName().en(),
                              airport.getCity().en(),
                              Point.from(airport.getCoordinates()),
                              airport.getTimezone());
    }
}
