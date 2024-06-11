package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.controllers.advice.DataNotFoundException;
import com.example.afs.flightdataapi.model.entities.AircraftsData;
import com.example.afs.flightdataapi.model.repositories.AircraftsDataRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

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

    @GetMapping("/aircraft/{code}")
    public ResponseEntity<AircraftsData> getAircraftDataById(@PathVariable String code) {
        AircraftsData found = findByAircraftCode(code);
        return ResponseEntity.ok(found);
    }

    private AircraftsData findByAircraftCode(String code) {
        return aircraftsDataRepository.findById(code)
                                      .orElseThrow(() -> new DataNotFoundException(code));
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

    @PutMapping("/aircraft/{code}")
    public ResponseEntity<AircraftsData> updateAircraftData(@PathVariable String code, @RequestBody AircraftsData updatedData) {
        AircraftsData toBeUpdated = findByAircraftCode(code);
        toBeUpdated.updateWith(updatedData);
        return ResponseEntity.ok(toBeUpdated);
    }

    @DeleteMapping("/aircraft/{code}")
    public ResponseEntity<AircraftsData> deleteAircraftData(@PathVariable String code) {
        AircraftsData deleted = findByAircraftCode(code);
        aircraftsDataRepository.deleteById(code);
        return ResponseEntity.ok(deleted);
    }
}
