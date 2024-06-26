package com.example.afs.flightdataapi.model.dto;

import com.example.afs.flightdataapi.model.entities.FareConditions;
import com.example.afs.flightdataapi.model.entities.Seat;
import com.example.afs.flightdataapi.model.entities.SeatId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record SeatDto(
        @NotBlank(message = AIRCRAFT_CODE_NULL_MESSAGE)
        @Length(min = 3, max = 3, message = AIRCRAFT_CODE_LENGTH_MESSAGE)
        String aircraftCode,

        @NotBlank(message = SEAT_NO_NULL_MESSAGE)
        @Length(min = 2, max = 4, message = SEAT_NO_LENGTH_MESSAGE)
        @Pattern(regexp = "^[0-9]+[A-Z]$", message = SEAT_NO_PATTERN_MESSAGE)
        String seatNo,

        @NotNull(message = FARE_CONDITIONS_NOT_NULL)
        FareConditions fareConditions) {

    public static final String AIRCRAFT_CODE_LENGTH_MESSAGE = "Aircraft code must be exactly 3 characters long";
    public static final String AIRCRAFT_CODE_NULL_MESSAGE = "An aircraft code must be provided";
    public static final String SEAT_NO_LENGTH_MESSAGE = "Seat number must be between 2 and 4 characters long";
    public static final String SEAT_NO_NULL_MESSAGE = "A seat number must be provided";
    public static final String SEAT_NO_PATTERN_MESSAGE = "Seat number must start one or more digits and end with a capital letter. Examples: 1A, 120G";
    public static final String FARE_CONDITIONS_NOT_NULL = "A fare conditions value must be provided. Possible values: BUSINESS, COMFORT, ECONOMY";

    public static SeatDto from(Seat seat) {
        SeatId seatId = seat.getSeatId();
        return new SeatDto(seatId.aircraftCode(), seatId.seatNo(), seat.getFareConditions());
    }

    public static List<SeatDto> from(List<Seat> seats) {
        return seats.stream()
                    .map(SeatDto::from)
                    .toList();
    }

    public SeatId seatId() {
        return new SeatId(aircraftCode, seatNo);
    }

}
