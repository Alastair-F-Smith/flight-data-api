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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api")
@Tag(name = "Aircraft data", description = "Data on aircraft models. Models are identified by a unique 3 character aircraft code.")
@SecurityRequirement(name = "bearerAuth")
@RestController
public class AircraftDataController {

    private final AircraftDataService aircraftDataService;
    private static final Logger logger = LoggerFactory.getLogger(AircraftDataController.class);

    public AircraftDataController(AircraftDataService aircraftDataService) {
        this.aircraftDataService = aircraftDataService;
    }

    @Operation(summary = "View all available aircraft models")
    @GetMapping("/aircraft")
    public ResponseEntity<CollectionModel<EntityModel<AircraftsData>>> getAllAircraft() {
        var aircraftModels = aircraftDataService.findAll()
                                                .stream()
                                                .map(aircraft -> EntityModel.of(aircraft)
                                                                            .add(linksForSingleAircraft(
                                                                                    aircraft.getAircraftCode())))
                                                .toList();

        var aircraftCollection = CollectionModel.of(aircraftModels)
                                                .add(linkTo(methodOn(AircraftDataController.class).getAllAircraft())
                                                             .withSelfRel()
                                                             .andAffordance(afford(methodOn(AircraftDataController.class)
                                                                                           .addAircraftData(null))));
        return ResponseEntity.ok(aircraftCollection);
    }

    @Operation(summary = "View a specified aircraft model")
    @GetMapping("/aircraft/{code}")
    public ResponseEntity<EntityModel<AircraftsData>> getAircraftDataById(@PathVariable String code) {
        AircraftsData found = findByAircraftCode(code);
        EntityModel<AircraftsData> aircraftModel = EntityModel.of(found).add(linksForAircraft(code));
        return ResponseEntity.ok(aircraftModel);
    }

    private AircraftsData findByAircraftCode(String code) {
        return aircraftDataService.findById(code)
                                  .orElseThrow(() -> new DataNotFoundException(code));
    }

    private List<Link> linksForAircraft(String code) {
        List<Link> links = linksForSingleAircraft(code);
        links.add(linkForAircraftCollective());
        return links;
    }

    private List<Link> linksForSingleAircraft(String code) {
        return new ArrayList<>(List.of(
                linkTo(methodOn(AircraftDataController.class).getAircraftDataById(code)).withSelfRel()
                    .andAffordance(afford(methodOn(AircraftDataController.class).updateAircraftData(code, null)))
                    .andAffordance(afford(methodOn(AircraftDataController.class).deleteAircraftData(code))),
                linkTo(methodOn(SeatController.class).getAllSeatsOnAircraft(code)).withRel("seats")
        ));
    }

    private Link linkForAircraftCollective() {
        return linkTo(methodOn(AircraftDataController.class).getAllAircraft()).withRel("allAircraft")
                    .andAffordance(afford(methodOn(AircraftDataController.class).addAircraftData(null)));
    }

    @Operation(summary = "Add a new aircraft model")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/aircraft")
    public ResponseEntity<EntityModel<AircraftsData>> addAircraftData(@Valid @RequestBody AircraftsData aircraftData) {
        AircraftsData saved = aircraftDataService.save(aircraftData);
        URI location = UriComponentsBuilder.fromHttpUrl("http://localhost/api/aircraft")
                                            .pathSegment(saved.getAircraftCode())
                                            .build().toUri();
        EntityModel<AircraftsData> aircraftModel = EntityModel.of(saved).add(linksForAircraft(saved.getAircraftCode()));
        return ResponseEntity.created(location)
                             .body(aircraftModel);
    }

    @Operation(summary = "Edit aircraft model data")
    @PutMapping("/aircraft/{code}")
    public ResponseEntity<EntityModel<AircraftsData>> updateAircraftData(@PathVariable String code,
                                                            @Valid @RequestBody AircraftsData updatedData) {
        AircraftsData toBeUpdated = findByAircraftCode(code);
        toBeUpdated.updateWith(updatedData);
        EntityModel<AircraftsData> aircraftModel = EntityModel.of(toBeUpdated).add(linksForAircraft(toBeUpdated.getAircraftCode()));
        return ResponseEntity.ok(aircraftModel);
    }

    @Operation(summary = "Delete data for an aircraft model")
    @DeleteMapping("/aircraft/{code}")
    public ResponseEntity<EntityModel<AircraftsData>> deleteAircraftData(@PathVariable String code) {
        AircraftsData deleted = findByAircraftCode(code);
        logger.debug("Found aircraft data for {}. Proceeding to delete...", code);
        aircraftDataService.deleteById(code);
        EntityModel<AircraftsData> aircraftModel = EntityModel.of(deleted).add(linkForAircraftCollective());
        return ResponseEntity.ok(aircraftModel);
    }
}
