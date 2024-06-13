package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.controllers.advice.DataNotFoundException;
import com.example.afs.flightdataapi.model.entities.AircraftsData;
import com.example.afs.flightdataapi.services.AircraftDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AircraftDataController {

    private final AircraftDataService aircraftDataService;
    private Logger logger = LoggerFactory.getLogger(AircraftDataController.class);

    public AircraftDataController(AircraftDataService aircraftDataService) {
        this.aircraftDataService = aircraftDataService;
    }

    @GetMapping("/aircraft")
    public ResponseEntity<List<AircraftsData>> getAllAircraft() {
        return ResponseEntity.ok(aircraftDataService.findAll());
    }

    @GetMapping("/aircraft/{code}")
    public ResponseEntity<AircraftsData> getAircraftDataById(@PathVariable String code) {
        AircraftsData found = findByAircraftCode(code);
        return ResponseEntity.ok(found);
    }

    private AircraftsData findByAircraftCode(String code) {
        return aircraftDataService.findById(code)
                                  .orElseThrow(() -> new DataNotFoundException(code));
    }

    @PostMapping("/aircraft")
    public ResponseEntity<AircraftsData> addAircraftData(@RequestBody AircraftsData aircraftData) {
        AircraftsData saved = aircraftDataService.save(aircraftData);
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
        logger.debug("Found aircraft data for {}. Proceding to delete...", code);
        aircraftDataService.deleteById(code);
        return ResponseEntity.ok(deleted);
    }
}
