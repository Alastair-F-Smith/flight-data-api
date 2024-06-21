package com.example.afs.flightdataapi.services.pricing;

import com.example.afs.flightdataapi.model.entities.FareConditions;

public record Fare(Integer flightId, FareConditions fareConditions) {
}
