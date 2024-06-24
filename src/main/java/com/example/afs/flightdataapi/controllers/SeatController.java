package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.controllers.advice.MismatchedIdentifierException;
import com.example.afs.flightdataapi.model.dto.SeatDto;
import com.example.afs.flightdataapi.model.entities.Seat;
import com.example.afs.flightdataapi.services.SeatService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SeatController implements SeatEndpoints {

    private final SeatService seatService;
    private final Logger logger = LoggerFactory.getLogger(SeatController.class);
    private final String baseUrl = "http://localhost:8080/api";

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @Override
    @GetMapping("/aircraft/{aircraftCode}/seats")
    public ResponseEntity<List<SeatDto>> getAllSeatsOnAircraft(@PathVariable String aircraftCode) {
        List<Seat> seats = seatService.findAllByAircraftCode(aircraftCode);
        logger.debug("Found {} seats on aircraft {}", seats.size(), aircraftCode);
        return ResponseEntity.ok(SeatDto.from(seats));
    }

    @Override
    @GetMapping("/aircraft/{aircraftCode}/seats/{seatNo}")
    public ResponseEntity<SeatDto> getSeat(@PathVariable String aircraftCode, @PathVariable String seatNo) {
        Seat seat = seatService.findById(aircraftCode, seatNo);
        logger.debug("Found seat number {} on aircraft {}", seatNo, aircraftCode);
        return ResponseEntity.ok(SeatDto.from(seat));
    }

    @Override
    @PostMapping("/aircraft/{aircraftCode}/seats")
    public ResponseEntity<SeatDto> addSeat(@PathVariable String aircraftCode,
                                           @Valid @RequestBody SeatDto seatDto) {
        checkIdentifiersMatch(aircraftCode, seatDto.aircraftCode());
        Seat seat = seatService.fromDto(seatDto);
        logger.debug("Attempting to save provided seat data {}...", seatDto.seatId());
        Seat saved = seatService.save(seat);
        URI location = UriComponentsBuilder.fromHttpUrl(baseUrl)
                                           .pathSegment("aircraft", aircraftCode, "seats")
                                           .build()
                                           .toUri();
        return ResponseEntity.created(location)
                             .body(SeatDto.from(saved));
    }

    private void checkIdentifiersMatch(String pathId, String bodyId) {
        if (!pathId.equals(bodyId)) {
            throw new MismatchedIdentifierException(pathId, bodyId);
        }
    }

    @Override
    @PutMapping("/aircraft/{aircraftCode}/seats/{seatNo}")
    public ResponseEntity<SeatDto> editSeat(@PathVariable String aircraftCode,
                                            @PathVariable String seatNo,
                                            @Valid @RequestBody SeatDto seatDto) {
        checkIdentifiersMatch(aircraftCode, seatDto.aircraftCode());
        checkIdentifiersMatch(seatNo, seatDto.seatNo());

        Seat updated = seatService.update(seatDto);
        return ResponseEntity.ok(SeatDto.from(updated));
    }

    @Override
    @DeleteMapping("/aircraft/{aircraftCode}/seats/{seatNo}")
    public ResponseEntity<SeatDto> deleteSeat(@PathVariable String aircraftCode,
                                            @PathVariable String seatNo) {

        Seat deleted = seatService.deleteById(aircraftCode, seatNo);
        return ResponseEntity.ok(SeatDto.from(deleted));
    }
}
