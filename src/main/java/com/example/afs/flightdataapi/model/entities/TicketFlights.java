package com.example.afs.flightdataapi.model.entities;

import com.example.afs.flightdataapi.services.converters.FareConditionsConverter;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
public class TicketFlights {

    @EmbeddedId
    private TicketFlightsId ticketFlightsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_no")
    @MapsId("ticketNo")
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    @MapsId("flightId")
    private Flight flight;

    @Convert(converter = FareConditionsConverter.class)
    private FareConditions fareConditions;

    private BigDecimal amount;

    public TicketFlights(Ticket ticket, Flight flight, FareConditions fareConditions, BigDecimal amount) {
        ticketFlightsId = new TicketFlightsId(ticket.getTicketNo(), flight.getFlightId());
        this.ticket = ticket;
        this.flight = flight;
        this.fareConditions = fareConditions;
        this.amount = amount;
    }

    public TicketFlights() {
    }

    public TicketFlightsId getTicketFlightsId() {
        return ticketFlightsId;
    }

    public void setTicketFlightsId(TicketFlightsId ticketFlightsId) {
        this.ticketFlightsId = ticketFlightsId;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public FareConditions getFareConditions() {
        return fareConditions;
    }

    public void setFareConditions(FareConditions fareConditions) {
        this.fareConditions = fareConditions;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TicketFlights that = (TicketFlights) o;
        return Objects.equals(ticketFlightsId, that.ticketFlightsId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ticketFlightsId);
    }

    @Override
    public String toString() {
        return "TicketFlights{" +
                "ticketFlightsId=" + ticketFlightsId +
                ", fareConditions=" + fareConditions +
                ", amount=" + amount +
                '}';
    }
}
