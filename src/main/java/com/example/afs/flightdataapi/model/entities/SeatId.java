package com.example.afs.flightdataapi.model.entities;

import jakarta.persistence.Embeddable;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Embeddable
public record SeatId(
        @Length(min = 3, max = 3, message = "Aircraft code must be exactly 3 characters long")
        String aircraftCode,
        @Length(min = 2, max = 4, message = "Seat number must be between 2 and 4 characters long")
        String seatNo
) implements Serializable {}
