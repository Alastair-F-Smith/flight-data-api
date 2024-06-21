package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.model.dto.FlightDetailsDto;
import com.example.afs.flightdataapi.model.dto.FlightQuery;
import com.example.afs.flightdataapi.model.dto.PagingAndSortingQuery;
import com.example.afs.flightdataapi.model.entities.Flight;
import com.example.afs.flightdataapi.services.FlightService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/flights")
    public ResponseEntity<Page<FlightDetailsDto>> getFlightPage(PagingAndSortingQuery pagingQuery) {
        Page<FlightDetailsDto> flights = flightService.findPage(pagingQuery.pageRequest())
                                                      .map(FlightDetailsDto::from);
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/flights/{flightId}")
    public ResponseEntity<FlightDetailsDto> getFlightById(@PathVariable Integer flightId) {
        Flight flight = flightService.findById(flightId);
        return ResponseEntity.ok(FlightDetailsDto.from(flight));
    }

    @GetMapping("flights/search")
    public ResponseEntity<Page<FlightDetailsDto>> searchFlights(FlightQuery flightQuery, PagingAndSortingQuery pagingAndSortingQuery) {
        Page<FlightDetailsDto> flights = flightService.search(flightQuery, pagingAndSortingQuery)
                                                      .map(FlightDetailsDto::from);
        return ResponseEntity.ok(flights);
    }

}
