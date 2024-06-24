package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.controllers.documentation.ExampleData;
import com.example.afs.flightdataapi.model.dto.SeatDto;
import com.example.afs.flightdataapi.model.entities.AircraftsData;
import com.example.afs.flightdataapi.model.entities.Seat;
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

@Tag(name = "Seat data", description = "Data on available seats within an aircraft.")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api")
public interface SeatEndpoints {

    @Operation(summary = "View all seats on a specified aircraft")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found the seat data",
                    content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Seat.class))) })
    })
    @GetMapping("/aircraft/{aircraftCode}/seats")
    ResponseEntity<List<SeatDto>> getAllSeatsOnAircraft(@PathVariable String aircraftCode);

    @Operation(summary = "View a selected seat on a specified aircraft model")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found the seat data",
                    content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Seat.class))) })
    })
    @GetMapping("/aircraft/{aircraftCode}/seats/{seatNo}")
    ResponseEntity<SeatDto> getSeat(@PathVariable String aircraftCode, @PathVariable String seatNo);

    @Operation(summary = "Add a seat to an aircraft")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Seat saved successfully",
                    content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Seat.class))) })
    })
    @PostMapping("/aircraft/{aircraftCode}/seats")
    ResponseEntity<SeatDto> addSeat(@PathVariable String aircraftCode,
                                    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = { @Content(examples = {
                                                    @ExampleObject(value = ExampleData.SEAT_VALID, name = "Valid data"),
                                                    @ExampleObject(value = ExampleData.SEAT_INVALID, name = "Invalid data")
                                    })})
                                    @Valid @RequestBody SeatDto seatDto);

    @Operation(summary = "Update seat information", description = "Note that the aircraft code and seat number in the URL path must match those in the request body")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Seat updated successfully",
                    content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Seat.class))) })
    })
    @PutMapping("/aircraft/{aircraftCode}/seats/{seatNo}")
    ResponseEntity<SeatDto> editSeat(@PathVariable String aircraftCode,
                                     @PathVariable String seatNo,
                                     @io.swagger.v3.oas.annotations.parameters.RequestBody(content = { @Content(examples = {
                                             @ExampleObject(value = ExampleData.SEAT_VALID, name = "Valid data"),
                                             @ExampleObject(value = ExampleData.SEAT_INVALID, name = "Invalid data")
                                     })})
                                     @Valid @RequestBody SeatDto seatDto);

    @Operation(summary = "Delete seat information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Seat deleted successfully",
                    content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Seat.class))) })
    })
    @DeleteMapping("/aircraft/{aircraftCode}/seats/{seatNo}")
    ResponseEntity<SeatDto> deleteSeat(@PathVariable String aircraftCode,
                                              @PathVariable String seatNo);
}
