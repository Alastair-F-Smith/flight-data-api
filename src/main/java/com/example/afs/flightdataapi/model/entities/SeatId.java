package com.example.afs.flightdataapi.model.entities;

import jakarta.persistence.Embeddable;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;

@Embeddable
public record SeatId(
        String aircraftCode,
        String seatNo) implements Serializable {}
