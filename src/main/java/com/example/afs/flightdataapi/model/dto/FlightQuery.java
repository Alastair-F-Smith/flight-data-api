package com.example.afs.flightdataapi.model.dto;

import java.time.ZonedDateTime;

public record FlightQuery(ZonedDateTime departureTime,
                          ZonedDateTime arrivalTime,
                          String departureAirport,
                          String arrivalAirport) {

}
