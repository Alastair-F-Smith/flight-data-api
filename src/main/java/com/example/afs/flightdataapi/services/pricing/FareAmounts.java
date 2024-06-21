package com.example.afs.flightdataapi.services.pricing;

import com.example.afs.flightdataapi.model.entities.FareConditions;
import com.example.afs.flightdataapi.services.converters.FareConditionsConverter;
import jakarta.persistence.Convert;

import java.math.BigDecimal;

public record FareAmounts(Integer flightId,
                          FareConditions fareConditions,
                          BigDecimal amount) {

    public FareAmounts(Integer flightId, FareConditions fareConditions, double amount) {
        this(flightId, fareConditions, BigDecimal.valueOf(amount));
    }
}
