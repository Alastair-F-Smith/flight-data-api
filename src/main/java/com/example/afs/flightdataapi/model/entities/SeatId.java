package com.example.afs.flightdataapi.model.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public record SeatId(String aircraftCode, String seatNo) implements Serializable {}
