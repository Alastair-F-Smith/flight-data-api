package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.controllers.advice.DataNotFoundException;
import com.example.afs.flightdataapi.model.entities.AircraftsData;
import com.example.afs.flightdataapi.services.AircraftDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RequestMapping("/api")
@Tag(name = "Aircraft data", description = "Data on aircraft models. Models are identified by a unique 3 character aircraft code.")
@SecurityRequirement(name = "bearerAuth")
@RestController
public class AircraftDataController {

    private final AircraftDataService aircraftDataService;
    private Logger logger = LoggerFactory.getLogger(AircraftDataController.class);

    public AircraftDataController(AircraftDataService aircraftDataService) {
        this.aircraftDataService = aircraftDataService;
    }

    @Operation(summary = "View all available aircraft models")
    @GetMapping("/aircraft")
    public ResponseEntity<List<AircraftsData>> getAllAircraft() {
        return ResponseEntity.ok(aircraftDataService.findAll());
    }

    @Operation(summary = "View a specified aircraft model")
    @GetMapping("/aircraft/{code}")
    public ResponseEntity<AircraftsData> getAircraftDataById(@PathVariable String code) {
        AircraftsData found = findByAircraftCode(code);
        return ResponseEntity.ok(found);
    }

    private AircraftsData findByAircraftCode(String code) {
        return aircraftDataService.findById(code)
                                  .orElseThrow(() -> new DataNotFoundException(code));
    }

    @Operation(summary = "Add a new aircraft model")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/aircraft")
    public ResponseEntity<AircraftsData> addAircraftData(@Valid @RequestBody AircraftsData aircraftData) {
        AircraftsData saved = aircraftDataService.save(aircraftData);
        URI location = UriComponentsBuilder.fromHttpUrl("http://localhost/api/aircraft")
                                            .pathSegment(saved.getAircraftCode())
                                            .build().toUri();
        return ResponseEntity.created(location)
                             .body(saved);
    }

    @Operation(summary = "Edit aircraft model data")
    @PutMapping("/aircraft/{code}")
    public ResponseEntity<AircraftsData> updateAircraftData(@PathVariable String code,
                                                            @Valid @RequestBody AircraftsData updatedData) {
        AircraftsData toBeUpdated = findByAircraftCode(code);
        toBeUpdated.updateWith(updatedData);
        return ResponseEntity.ok(toBeUpdated);
    }

    @Operation(summary = "Delete data for an aircraft model")
    @DeleteMapping("/aircraft/{code}")
    public ResponseEntity<AircraftsData> deleteAircraftData(@PathVariable String code) {
        AircraftsData deleted = findByAircraftCode(code);
        logger.debug("Found aircraft data for {}. Proceding to delete...", code);
        aircraftDataService.deleteById(code);
        return ResponseEntity.ok(deleted);
    }
}
