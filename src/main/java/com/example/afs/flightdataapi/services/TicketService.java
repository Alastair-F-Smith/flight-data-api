package com.example.afs.flightdataapi.services;

import com.example.afs.flightdataapi.controllers.advice.DataNotFoundException;
import com.example.afs.flightdataapi.model.entities.FareConditions;
import com.example.afs.flightdataapi.model.entities.Flight;
import com.example.afs.flightdataapi.model.entities.Ticket;
import com.example.afs.flightdataapi.model.entities.TicketFlights;
import com.example.afs.flightdataapi.model.repositories.TicketFlightsRepository;
import com.example.afs.flightdataapi.model.repositories.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketFlightsRepository ticketFlightsRepository;
    private final FlightService flightService;

    public TicketService(TicketRepository ticketRepository, TicketFlightsRepository ticketFlightsRepository, FlightService flightService) {
        this.ticketRepository = ticketRepository;
        this.ticketFlightsRepository = ticketFlightsRepository;
        this.flightService = flightService;
    }

    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    public Ticket findById(String ticketNo) {
        return ticketRepository.findById(ticketNo)
                .orElseThrow(() -> new DataNotFoundException(ticketNo));
    }

    @Transactional
    public Ticket save(Ticket ticket) {
        if (ticket.getTicketNo() == null) {
            ticket.setTicketNo(ticketRepository.findMaxTicketNo() + 1L);
        }
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket addFlight(String ticketNo, int flightId, FareConditions fareConditions, BigDecimal amount) {
        Ticket ticket = findById(ticketNo);
        Flight flight = flightService.findById(flightId);
        TicketFlights ticketFlight = new TicketFlights(ticket, flight, fareConditions, amount);
        ticketFlightsRepository.save(ticketFlight);
        ticket.addTicketFlight(ticketFlight);
        return ticketRepository.save(ticket);
    }

    public Ticket delete(String ticketNo) {
        Ticket ticket = findById(ticketNo);
        ticketRepository.delete(ticket);
        return ticket;
    }
}
