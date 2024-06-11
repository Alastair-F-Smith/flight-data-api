package com.example.afs.flightdataapi.controllers.advice;

public class DataNotFoundException extends RuntimeException {

    public DataNotFoundException(String id) {
        super("Record with id " + id + " could not be found.");
    }
}
