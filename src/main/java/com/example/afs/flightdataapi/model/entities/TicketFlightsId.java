package com.example.afs.flightdataapi.model.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public record TicketFlightsId(String ticketNo, Integer flightId) implements Serializable {
}
