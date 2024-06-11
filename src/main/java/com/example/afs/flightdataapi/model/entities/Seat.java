package com.example.afs.flightdataapi.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat {
    @EmbeddedId
    SeatId seatId;

    @MapsId("aircraftCode")
    @JoinColumn(name = "aircraft_code")
    @ManyToOne
    AircraftsData aircraft;

    @Convert(converter = FareConditionsConverter.class)
    FareConditions fareConditions;

    public Seat(SeatId seatId, AircraftsData aircraft, FareConditions fareConditions) {
        this.seatId = seatId;
        this.aircraft = aircraft;
        this.fareConditions = fareConditions;
    }

    public Seat() {
    }

    public SeatId getSeatId() {
        return seatId;
    }

    public void setSeatId(SeatId seatId) {
        this.seatId = seatId;
    }

    public AircraftsData getAircraft() {
        return aircraft;
    }

    public void setAircraft(AircraftsData aircraft) {
        this.aircraft = aircraft;
    }

    public FareConditions getFareConditions() {
        return fareConditions;
    }

    public void setFareConditions(FareConditions fareConditions) {
        this.fareConditions = fareConditions;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "seatId=" + seatId +
                ", aircraft=" + aircraft +
                ", fareConditions=" + fareConditions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Seat seat = (Seat) o;
        return seatId.equals(seat.seatId);
    }

    @Override
    public int hashCode() {
        return seatId.hashCode();
    }
}
