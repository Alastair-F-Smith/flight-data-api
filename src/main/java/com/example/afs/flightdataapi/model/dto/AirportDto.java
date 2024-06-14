package com.example.afs.flightdataapi.model.dto;

import com.example.afs.flightdataapi.model.entities.Airport;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.TimeZone;

public record AirportDto(

        @NotBlank(message = AIRPORT_CODE_NULL_MESSAGE)
        @Length(min = 3, max = 3, message = AIRPORT_CODE_LENGTH_MESSAGE)
        String airportCode,

        @NotBlank(message = AIRPORT_NAME_MESSAGE)
        String name,

        @NotBlank(message = CITY_BLANK_MESSAGE)
        String city,

        @NotNull(message = COORDINATES_NULL_MESSAGE)
        @Valid
        Point coordinates,

        @NotNull(message = TIMEZONE_NULL_MESSAGE)
        TimeZone timeZone) {

    public static final String AIRPORT_CODE_LENGTH_MESSAGE = "Airport code must be exactly 3 characters long";
    public static final String AIRPORT_CODE_NULL_MESSAGE = "An airport code must be provided";
    public static final String AIRPORT_NAME_MESSAGE = "An airport name must be provided and cannot be blank";
    public static final String CITY_BLANK_MESSAGE = "A city name must be provided and cannot be blank";
    public static final String COORDINATES_NULL_MESSAGE = "Coordinates must be provided";
    public static final String TIMEZONE_NULL_MESSAGE = "A timezone must be provided e.g. 'Europe/London'";

    public static AirportDto from(Airport airport) {
        return new AirportDto(airport.getAirportCode(),
                              airport.getAirportName().en(),
                              airport.getCity().en(),
                              Point.from(airport.getCoordinates()),
                              airport.getTimezone());
    }

    public static List<AirportDto> from(List<Airport> airports) {
        return airports.stream()
                       .map(AirportDto::from)
                       .toList();
    }
}
