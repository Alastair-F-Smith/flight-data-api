package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.model.entities.Seat;
import com.example.afs.flightdataapi.services.SeatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<Seat>> getAllSeatsOnAircraft(@PathVariable String aircraftCode) {
        List<Seat> seats = seatService.findAllByAircraftCode(aircraftCode);
        logger.debug("Found {} seats on aircraft {}", seats.size(), aircraftCode);
        return ResponseEntity.ok(seats);
    }

}
