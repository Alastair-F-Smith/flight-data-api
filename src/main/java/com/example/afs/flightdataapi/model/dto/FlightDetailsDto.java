package com.example.afs.flightdataapi.model.dto;

import com.example.afs.flightdataapi.model.entities.Flight;

import java.time.ZonedDateTime;

public record FlightDetailsDto(
        int flightId,
        String flightNo,
        ZonedDateTime scheduledDeparture,
        ZonedDateTime actualDeparture,
        ZonedDateTime scheduledArrival,
        ZonedDateTime actualArrival,
        AirportDto departureAirport,
        AirportDto arrivalAirport,
        String status,
        AircraftDto aircraft
) {

    public static FlightDetailsDto from(Flight entity) {
        return new FlightDetailsDto(entity.getFlightId(),
                                    entity.getFlightNo(),
                                    entity.getScheduledDeparture(),
                                    entity.getActualDeparture(),
                                    entity.getScheduledArrival(),
                                    entity.getActualArrival(),
                                    AirportDto.from(entity.getDepartureAirport()),
                                    AirportDto.from(entity.getArrivalAirport()),
                                    entity.getStatus(),
                                    AircraftDto.from(entity.getAircraftCode()));
    }
}
