package com.example.afs.flightdataapi.model.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class TicketTests {

    @RepeatedTest(10)
    @DisplayName("Generated ticket numbers have a length of 13 characters")
    void generatedTicketNumbersHaveALengthOf13Characters() {
        String ticketNo = Ticket.generateTicketNo();
        System.out.println(ticketNo);
        assertThat(ticketNo.length()).isEqualTo(13);
    }

    @Test
    @DisplayName("Generated ticket numbers consist only of digits")
    void generatedTicketNumbersConsistOnlyOfDigits() {
        String ticketNo = Ticket.generateTicketNo();
        assertThat(ticketNo).containsPattern("^[0-9]{13}$");
    }

    @RepeatedTest(10)
    @DisplayName("Generated passenger numbers have a length of 11 characters")
    void generatedPassengerNumbersHaveALengthOf11Characters() {
         String passengerNo = Ticket.generatePassengerId();
        System.out.println(passengerNo);
         assertThat(passengerNo.length()).isEqualTo(11);
    }

    @Test
    @DisplayName("Generated passenger numbers consist of two strings separated by a space")
    void generatedPassengerNumbersConsistOfTwoStringsSeparatedByASpace() {
        String passengerNo = Ticket.generatePassengerId();
        assertThat(passengerNo).containsPattern("^[0-9]{4} [0-9]{6}$");
    }



}