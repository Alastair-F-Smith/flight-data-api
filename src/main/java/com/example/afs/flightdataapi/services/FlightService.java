package com.example.afs.flightdataapi.services;

import com.example.afs.flightdataapi.controllers.advice.DataNotFoundException;
import com.example.afs.flightdataapi.model.dto.FlightSummaryDto;
import com.example.afs.flightdataapi.model.entities.Flight;
import com.example.afs.flightdataapi.model.entities.Ticket;
import com.example.afs.flightdataapi.model.repositories.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
