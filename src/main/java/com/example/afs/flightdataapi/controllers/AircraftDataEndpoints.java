package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.controllers.documentation.ExampleData;
import com.example.afs.flightdataapi.model.entities.AircraftsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/api")
@Tag(name = "Aircraft data", description = "Data on aircraft models. Models are identified by a unique 3 character aircraft code.")
@SecurityRequirement(name = "bearerAuth")
public interface AircraftDataEndpoints {

    @Operation(summary = "View all available aircraft models")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found the available aircraft",
                    content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AircraftsData.class))) })
    })
    @GetMapping("/aircraft")
    ResponseEntity<List<AircraftsData>> getAllAircraft();

    @Operation(summary = "View a specified aircraft model")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Model found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = AircraftsData.class))})
    })
    @GetMapping("/aircraft/{code}")
    ResponseEntity<AircraftsData> getAircraftDataById(@PathVariable String code);

    @Operation(summary = "Add a new aircraft model")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Model created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AircraftsData.class))})
    })
    @PostMapping("/aircraft")
    ResponseEntity<AircraftsData> addAircraftData(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Aircraft data to be added",
                    content = { @Content(examples = {@ExampleObject(value = ExampleData.AIRCRAFT_VALID, name = "Valid data"),
                            @ExampleObject(value = ExampleData.AIRCRAFT_INVALID, name = "Invalid data")
                    })})
            @Valid @RequestBody AircraftsData aircraftData);

    @Operation(summary = "Edit aircraft model data")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Model updated",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = AircraftsData.class))})
    })
    @PutMapping("/aircraft/{code}")
    ResponseEntity<AircraftsData> updateAircraftData(@PathVariable String code,
                                                    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Aircraft data to be added",
                                                            content = { @Content(examples = {@ExampleObject(value = ExampleData.AIRCRAFT_VALID, name = "Valid data"),
                                                                    @ExampleObject(value = ExampleData.AIRCRAFT_INVALID, name = "Invalid data")
                                                            })})
                                                    @Valid @RequestBody AircraftsData updatedData);

    @Operation(summary = "Delete data for an aircraft model")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Model deleted",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = AircraftsData.class))})
    })
    @DeleteMapping("/aircraft/{code}")
    ResponseEntity<AircraftsData> deleteAircraftData(@PathVariable String code);
}
