package com.example.afs.flightdataapi.model.dto;

import com.example.afs.flightdataapi.model.entities.Booking;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public record BookingDto(String bookRef,
                         ZonedDateTime bookDate,
                         BigDecimal totalAmount,
                         List<PersonalDetailsDto> people,
                         List<FlightSummaryDto> flights) {

    public static BookingDto from(Booking booking, List<PersonalDetailsDto> people, List<FlightSummaryDto> flights) {
        return new BookingDto(booking.getBookRef(),
                              booking.getBookDate(),
                              booking.getTotalAmount(),
                              people,
                              flights);
    }

    public static BookingDto from(Booking booking) {
        return from(booking, new ArrayList<>(), new ArrayList<>());
    }
}
