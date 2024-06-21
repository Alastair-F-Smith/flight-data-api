package com.example.afs.flightdataapi.model.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withinPercentage;
import static org.junit.jupiter.api.Assertions.fail;

class BookingTests {

    @RepeatedTest(10)
    @DisplayName("Generate book ref generates valid references")
    void generateBookRefGeneratesValidReferences() {
        String ref = Booking.generateBookRef();
        System.out.println(ref);
        assertThat(ref).containsPattern("^[0-9A-F]{6}$");
    }

    @Test
    @DisplayName("Set total amount correctly updates the total amount from a list of tickets")
    void setTotalAmountCorrectlyUpdatesTheTotalAmountFromAListOfTickets() {
        Ticket t1 = new Ticket();
        Ticket t2 = new Ticket();
        TicketFlights tf1 = new TicketFlights();
        TicketFlights tf2 = new TicketFlights();
        TicketFlights tf3 = new TicketFlights();

        tf1.setAmount(BigDecimal.valueOf(1000));
        tf2.setAmount(BigDecimal.valueOf(5000));
        tf3.setAmount(BigDecimal.valueOf(3000));

        t1.setTicketFlights(List.of(tf1, tf2));
        t2.setTicketFlights(List.of(tf3));

        Booking booking = new Booking();
        booking.calculateTotalAmount(List.of(t1, t2));

        assertThat(booking.getTotalAmount()).isCloseTo(BigDecimal.valueOf(9000), withinPercentage(1));
    }

}