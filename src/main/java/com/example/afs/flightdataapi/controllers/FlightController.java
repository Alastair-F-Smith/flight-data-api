package com.example.afs.flightdataapi.controllers;

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
    public ResponseEntity<Page<Flight>> getFlightPage(PagingAndSortingQuery pagingQuery) {
        Page<Flight> flights = flightService.findPage(pagingQuery.pageRequest());
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/flights/{flightId}")
    public ResponseEntity<Flight> getFlightById(@PathVariable Integer flightId) {
        Flight flight = flightService.findById(flightId);
        return ResponseEntity.ok(flight);
    }

    @GetMapping("flights/search")
    public ResponseEntity<Page<Flight>> searchFlights(FlightQuery flightQuery, PagingAndSortingQuery pagingAndSortingQuery) {
        Page<Flight> flights = flightService.search(flightQuery, pagingAndSortingQuery);
        return ResponseEntity.ok(flights);
    }

}
