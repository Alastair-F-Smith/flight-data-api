package com.example.afs.flightdataapi.services;

import com.example.afs.flightdataapi.controllers.advice.DataNotFoundException;
import com.example.afs.flightdataapi.model.entities.Flight;
import com.example.afs.flightdataapi.model.entities.Ticket;
import com.example.afs.flightdataapi.model.repositories.FlightRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final TicketService ticketService;

    public FlightService(FlightRepository flightRepository, TicketService ticketService) {
        this.flightRepository = flightRepository;
        this.ticketService = ticketService;
    }

    public List<Flight> findAll() {
        return flightRepository.findAll();
    }

    public Flight findById(int flightId) {
        return flightRepository.findById(flightId)
                .orElseThrow(() -> new DataNotFoundException(flightId));
    }

    public Flight save(Flight flight) {
        return flightRepository.save(flight);
    }

    public Flight delete(int flightId) {
        Flight flight = findById(flightId);
        List<Ticket> tickets = ticketService.findByFlightId(flightId);
        ticketService.delete(tickets);
        flightRepository.delete(flight);
        return flight;
    }
}
