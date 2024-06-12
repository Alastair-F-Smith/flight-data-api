package com.example.afs.flightdataapi.model.dto;

import com.example.afs.flightdataapi.model.entities.FareConditions;
import com.example.afs.flightdataapi.model.entities.Seat;
import com.example.afs.flightdataapi.model.entities.SeatId;

import java.util.Collection;
import java.util.List;

public record SeatDto(String aircraftCode, String seatNo, FareConditions fareConditions) {

    public static SeatDto from(Seat seat) {
        SeatId seatId = seat.getSeatId();
        return new SeatDto(seatId.aircraftCode(), seatId.seatNo(), seat.getFareConditions());
    }

    public static List<SeatDto> from(List<Seat> seats) {
        return seats.stream()
                    .map(SeatDto::from)
                    .toList();
    }

}
