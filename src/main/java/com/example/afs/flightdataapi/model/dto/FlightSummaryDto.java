package com.example.afs.flightdataapi.model.dto;

import com.example.afs.flightdataapi.model.entities.Flight;

import java.time.ZonedDateTime;

public record FlightSummaryDto(String flightNo,
                               ZonedDateTime scheduledDeparture,
                               ZonedDateTime scheduledArrival,
                               String departureAirport,
                               String arrivalAirport) {

    public static FlightSummaryDto from(Flight flight) {
        return new FlightSummaryDto(flight.getFlightNo(),
                                    flight.getScheduledDeparture(),
                                    flight.getScheduledArrival(),
                                    flight.getDepartureAirport().getAirportCode(),
                                    flight.getArrivalAirport().getAirportCode());
    }
}
