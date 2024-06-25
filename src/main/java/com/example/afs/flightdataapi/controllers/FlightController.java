package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.model.dto.FlightDetailsDto;
import com.example.afs.flightdataapi.model.dto.FlightQuery;
import com.example.afs.flightdataapi.model.dto.PagingAndSortingQuery;
import com.example.afs.flightdataapi.model.entities.Flight;
import com.example.afs.flightdataapi.services.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Flight data", description = "Search for flights")
@RestController
@RequestMapping("/api")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @Operation(summary = "View all available flights")
    @PageableAsQueryParam
    @GetMapping("/flights")
    public ResponseEntity<Page<FlightDetailsDto>> getFlightPage(@Parameter(hidden = true) Pageable pagingQuery) {
        Page<FlightDetailsDto> flights = flightService.findPage(pagingQuery)
                                                      .map(FlightDetailsDto::from);
        return ResponseEntity.ok(flights);
    }

    @Operation(summary = "View a specified flight")
    @GetMapping("/flights/{flightId}")
    public ResponseEntity<FlightDetailsDto> getFlightById(@PathVariable Integer flightId) {
        Flight flight = flightService.findById(flightId);
        return ResponseEntity.ok(FlightDetailsDto.from(flight));
    }

    @Operation(summary = "Search for flights",
            description = "Arrival and departure airports can be search by airport or city name or by airport code. " +
                            "The service holds data for flights departing between 2017-07-15 and 2017-09-14.")
    @PageableAsQueryParam
    @GetMapping("flights/search")
    public ResponseEntity<Page<FlightDetailsDto>> searchFlights(FlightQuery flightQuery,
                                                                @Parameter(hidden = true) Pageable pagingAndSortingQuery) {
        Page<FlightDetailsDto> flights = flightService.search(flightQuery, pagingAndSortingQuery)
                                                      .map(FlightDetailsDto::from);
        return ResponseEntity.ok(flights);
    }

}
