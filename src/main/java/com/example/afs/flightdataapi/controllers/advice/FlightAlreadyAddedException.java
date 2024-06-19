package com.example.afs.flightdataapi.controllers.advice;

public class FlightAlreadyAddedException extends RuntimeException {

    public FlightAlreadyAddedException(int flightId, String bookRef) {
        super("Flight %d has already been added to booking %s".formatted(flightId, bookRef));
    }
}
