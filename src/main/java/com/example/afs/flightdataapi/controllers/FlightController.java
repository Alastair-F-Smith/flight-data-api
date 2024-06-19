package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.model.entities.Flight;
import com.example.afs.flightdataapi.services.FlightService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/flights")
    public ResponseEntity<Page<Flight>> getFlightPage(@RequestParam(required = false, defaultValue = "20") int pageSize,
                                                      @RequestParam(required = false, defaultValue = "0") int pageNumber,
                                                      @RequestParam(required = false, defaultValue = "scheduledDeparture") String sortField,
                                                      @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Page<Flight> flights = flightService.findPage(pageNumber, pageSize, sortField, sortDirection);
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/flights/{flightId}")
    public ResponseEntity<Flight> getFlightById(@PathVariable Integer flightId) {
        Flight flight = flightService.findById(flightId);
        return ResponseEntity.ok(flight);
    }

}
