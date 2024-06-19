package com.example.afs.flightdataapi.services;

import com.example.afs.flightdataapi.controllers.advice.DataNotFoundException;
import com.example.afs.flightdataapi.model.dto.FlightSummaryDto;
import com.example.afs.flightdataapi.model.entities.Flight;
import com.example.afs.flightdataapi.model.entities.Ticket;
import com.example.afs.flightdataapi.model.repositories.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

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
}
