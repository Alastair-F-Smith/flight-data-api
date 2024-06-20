package com.example.afs.flightdataapi.services;

import com.example.afs.flightdataapi.controllers.advice.DataNotFoundException;
import com.example.afs.flightdataapi.model.dto.FlightQuery;
import com.example.afs.flightdataapi.model.dto.PagingAndSortingQuery;
import com.example.afs.flightdataapi.model.entities.Flight;
import com.example.afs.flightdataapi.model.entities.FlightSpecs;
import com.example.afs.flightdataapi.model.repositories.FlightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private static final Logger logger = LoggerFactory.getLogger(FlightService.class);

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public List<Flight> findAll() {
        return flightRepository.findAll();
    }

    public Page<Flight> findPage(Pageable pageable) {
        return flightRepository.findAll(pageable);
    }

    public Page<Flight> findPage(int pageNumber, int pageSize, String sortField, String sortDirection) {
        Sort sort = Sort.by(sortField);
        sort = isAscending(sortDirection) ? sort.ascending() : sort.descending();
        return flightRepository.findAll(PageRequest.of(pageNumber, pageSize, sort));
    }

    private boolean isAscending(String sortDirection) {
        return sortDirection.toLowerCase().contains("asc");
    }

    public Flight findById(int flightId) {
        return flightRepository.findById(flightId)
                .orElseThrow(() -> new DataNotFoundException(flightId));
    }

    public List<Flight> findByBookRef(String bookRef) {
        return flightRepository.findFlightsByBookRef(bookRef);
    }

    public Flight save(Flight flight) {
        return flightRepository.save(flight);
    }

    public Flight delete(Flight flight) {
        flightRepository.delete(flight);
        return flight;
    }

    public List<Flight> delete(List<Flight> flights) {
        flightRepository.deleteAll(flights);
        return flights;
    }

    public Page<Flight> search(FlightQuery query, PagingAndSortingQuery paging) {
        logger.debug("Search for flights with the query: {}", query);
        FlightSpecs spec = new FlightSpecs(query);
        return flightRepository.findAll(spec, paging.pageRequest());
    }

    public void deleteByAirportCode(String airportCode) {
        List<Flight> flights = findFlightsArrivingOrDepartingFromAirport(airportCode);
        delete(flights);
    }

    private List<Flight> findFlightsArrivingOrDepartingFromAirport(String airportCode) {
        Specification<Flight> spec = FlightSpecs.byArrivalAirport(airportCode)
                                                .or(FlightSpecs.byDepartureAirport(airportCode));
        return flightRepository.findAll(spec);
    }

    public void deleteByAircraftCode(String aircraftCode) {
        List<Flight> flights = flightRepository.findByAircraftCode_AircraftCode(aircraftCode);
        delete(flights);
    }
}
