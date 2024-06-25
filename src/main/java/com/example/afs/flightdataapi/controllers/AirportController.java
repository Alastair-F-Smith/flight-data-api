package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.controllers.advice.MismatchedIdentifierException;
import com.example.afs.flightdataapi.model.dto.AirportDto;
import com.example.afs.flightdataapi.model.entities.Airport;
import com.example.afs.flightdataapi.services.AirportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Airport data", description = "Data on available airports.")
@RestController
@RequestMapping("/api")
public class AirportController {

    private final AirportService airportService;

    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @Operation(summary = "Get data on all available airports")
    @GetMapping(value = "/airports")
    public ResponseEntity<List<AirportDto>> getAllAirports() {
        List<Airport> airports = airportService.findAll();
        return ResponseEntity.ok(AirportDto.from(airports));
    }

    @Operation(summary = "Get data on a specified airport", description = "The airport code is a unique 3-character identifier")
    @GetMapping("/airports/{airportCode}")
    public ResponseEntity<AirportDto> getByAirportCode(@PathVariable String airportCode) {
        Airport airport = airportService.findById(airportCode);
        return ResponseEntity.ok(AirportDto.from(airport));
    }

    @Operation(summary = "Add an airport")
    @ResponseStatus(HttpStatus.CREATED)
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

    @Operation(summary = "Edit data for an airport", description = "The airport codes in the path and the request body must match.")
    @PutMapping("/airports/{airportCode}")
    public ResponseEntity<AirportDto> updateAirport(@PathVariable String airportCode, @Valid @RequestBody AirportDto airportDto) {
        if (!airportCode.equals(airportDto.airportCode())) {
            throw new MismatchedIdentifierException(airportCode, airportDto.airportCode());
        }
        Airport airport = airportService.fromDto(airportDto);
        Airport updated = airportService.save(airport);
        return ResponseEntity.ok(AirportDto.from(updated));
    }

    @Operation(summary = "Remove data for an airport")
    @DeleteMapping("airports/{airportCode}")
    public ResponseEntity<AirportDto> deleteAirport(@PathVariable String airportCode) {
        Airport deleted = airportService.delete(airportCode);
        return ResponseEntity.ok(AirportDto.from(deleted));
    }
}
