package com.example.afs.flightdataapi.model.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookingTests {

    @RepeatedTest(10)
    @DisplayName("Generate book ref generates valid references")
    void generateBookRefGeneratesValidReferences() {
        String ref = Booking.generateBookRef();
        System.out.println(ref);
        assertThat(ref).containsPattern("^[0-9A-F]{6}$");
    }

}