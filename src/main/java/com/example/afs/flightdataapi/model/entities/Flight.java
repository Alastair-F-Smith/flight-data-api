package com.example.afs.flightdataapi.model.entities;

import jakarta.persistence.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "flights")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "flight_seq")
    @SequenceGenerator(name = "flight_seq", sequenceName = "flights_flight_id_seq", allocationSize = 1)
    int flightId;

    String flightNo;

    ZonedDateTime scheduledDeparture;
    ZonedDateTime scheduledArrival;

    @ManyToOne
    @JoinColumn(name = "departureAirport", referencedColumnName = "airportCode")
    Airport departureAirport;

    @ManyToOne
    @JoinColumn(name = "arrivalAirport", referencedColumnName = "airportCode")
    Airport arrivalAirport;
    String status;

    @ManyToOne
    @JoinColumn(name = "aircraftCode")
    AircraftsData aircraftCode;
    ZonedDateTime actualDeparture;
    ZonedDateTime actualArrival;

    public Flight(String flightNo, ZonedDateTime scheduledDeparture, ZonedDateTime scheduledArrival, Airport departureAirport, Airport arrivalAirport, String status, AircraftsData aircraftCode, ZonedDateTime actualDeparture, ZonedDateTime actualArrival) {
        this.flightNo = flightNo;
        this.scheduledDeparture = scheduledDeparture;
        this.scheduledArrival = scheduledArrival;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.status = status;
        this.aircraftCode = aircraftCode;
        this.actualDeparture = actualDeparture;
        this.actualArrival = actualArrival;
    }

    public Flight() {
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public String getFlightNo() {
        return flightNo;
    }

    public void setFlightNo(String flightNo) {
        this.flightNo = flightNo;
    }

    public ZonedDateTime getScheduledDeparture() {
        return scheduledDeparture;
    }

    public void setScheduledDeparture(ZonedDateTime scheduledDeparture) {
        this.scheduledDeparture = scheduledDeparture;
    }

    public ZonedDateTime getScheduledArrival() {
        return scheduledArrival;
    }

    public void setScheduledArrival(ZonedDateTime scheduledArrival) {
        this.scheduledArrival = scheduledArrival;
    }

    public Airport getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(Airport departureAirport) {
        this.departureAirport = departureAirport;
    }

    public Airport getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(Airport arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AircraftsData getAircraftCode() {
        return aircraftCode;
    }

    public void setAircraftCode(AircraftsData aircraftCode) {
        this.aircraftCode = aircraftCode;
    }

    public ZonedDateTime getActualDeparture() {
        return actualDeparture;
    }

    public void setActualDeparture(ZonedDateTime actualDeparture) {
        this.actualDeparture = actualDeparture;
    }

    public ZonedDateTime getActualArrival() {
        return actualArrival;
    }

    public void setActualArrival(ZonedDateTime actualArrival) {
        this.actualArrival = actualArrival;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Flight flight = (Flight) o;
        return flightId == flight.flightId;
    }

    @Override
    public int hashCode() {
        return flightId;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "flightId=" + flightId +
                ", flightNo='" + flightNo + '\'' +
                ", scheduledDeparture=" + scheduledDeparture +
                ", scheduledArrival=" + scheduledArrival +
                ", departureAirport=" + departureAirport +
                ", arrivalAirport=" + arrivalAirport +
                ", status='" + status + '\'' +
                ", aircraftCode=" + aircraftCode +
                ", actualDeparture=" + actualDeparture +
                ", actualArrival=" + actualArrival +
                '}';
    }
}
