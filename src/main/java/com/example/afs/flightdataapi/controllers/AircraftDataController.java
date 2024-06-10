package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.model.entities.AircraftsData;
import com.example.afs.flightdataapi.model.repositories.AircraftsDataRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AircraftDataController {

    private final AircraftsDataRepository aircraftsDataRepository;

    public AircraftDataController(AircraftsDataRepository aircraftsDataRepository) {
        this.aircraftsDataRepository = aircraftsDataRepository;
    }

    @GetMapping("/aircraft")
    public ResponseEntity<List<AircraftsData>> getAllAircraft() {
        return ResponseEntity.ok(aircraftsDataRepository.findAll());
    }

    @PostMapping("/aircraft")
    public ResponseEntity<AircraftsData> addAircraftData(@RequestBody AircraftsData aircraftData) {
        AircraftsData saved = aircraftsDataRepository.save(aircraftData);
        URI location = UriComponentsBuilder.fromHttpUrl("http://localhost/api/aircraft")
                                            .pathSegment(saved.getAircraftCode())
                                            .build().toUri();
        return ResponseEntity.created(location)
                             .body(saved);
    }
}
