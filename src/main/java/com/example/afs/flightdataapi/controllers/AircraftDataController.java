package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.model.entities.AircraftsData;
import com.example.afs.flightdataapi.model.repositories.AircraftsDataRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
