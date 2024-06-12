package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.model.dto.SeatDto;
import com.example.afs.flightdataapi.model.entities.Seat;
import com.example.afs.flightdataapi.services.SeatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SeatController {

    private final SeatService seatService;
    private final Logger logger = LoggerFactory.getLogger(SeatController.class);

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping("/aircraft/{aircraftCode}/seats")
    public ResponseEntity<List<SeatDto>> getAllSeatsOnAircraft(@PathVariable String aircraftCode) {
        List<Seat> seats = seatService.findAllByAircraftCode(aircraftCode);
        logger.debug("Found {} seats on aircraft {}", seats.size(), aircraftCode);
        return ResponseEntity.ok(SeatDto.from(seats));
    }

    @GetMapping("/aircraft/{aircraftCode}/seats/{seatNo}")
    public ResponseEntity<SeatDto> getSeat(@PathVariable String aircraftCode, @PathVariable String seatNo) {
        Seat seat = seatService.findById(aircraftCode, seatNo);
        logger.debug("Found seat number {} on aircraft {}", seatNo, aircraftCode);
        return ResponseEntity.ok(SeatDto.from(seat));
    }

}
