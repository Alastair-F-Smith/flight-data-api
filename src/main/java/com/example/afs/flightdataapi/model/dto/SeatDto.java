package com.example.afs.flightdataapi.model.dto;

import com.example.afs.flightdataapi.model.entities.FareConditions;
import com.example.afs.flightdataapi.model.entities.Seat;
import com.example.afs.flightdataapi.model.entities.SeatId;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.util.Collection;
import java.util.List;

public record SeatDto(
        @Length(min = 3, max = 3, message = "Aircraft code must be exactly 3 characters long")
        String aircraftCode,
        @Length(min = 2, max = 4, message = "Seat number must be between 2 and 4 characters long")
        String seatNo,
        FareConditions fareConditions) {

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
