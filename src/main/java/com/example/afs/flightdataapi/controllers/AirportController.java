package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.model.dto.AirportDto;
import com.example.afs.flightdataapi.model.entities.Airport;
import com.example.afs.flightdataapi.services.AirportService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AirportController {

    private final AirportService airportService;

    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @GetMapping("/airports")
    public ResponseEntity<List<AirportDto>> getAllAirports() {
        List<Airport> airports = airportService.findAll();
        return ResponseEntity.ok(AirportDto.from(airports));
    }

    @GetMapping("/airports/{airportCode}")
    public ResponseEntity<AirportDto> getByAirportCode(@PathVariable String airportCode) {
        Airport airport = airportService.findById(airportCode);
        return ResponseEntity.ok(AirportDto.from(airport));
    }

    @PostMapping("/airports")
    public ResponseEntity<AirportDto> saveAirport(@Valid @RequestBody AirportDto airportDto) {
        Airport airport = airportService.fromDto(airportDto);
        Airport saved = airportService.save(airport);

        URI location = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/api")
                                           .pathSegment("airports", saved.getAirportCode())
                                           .build()
                                           .toUri();

        return ResponseEntity.created(location)
                             .body(AirportDto.from(saved));
    }
}
