package com.example.afs.flightdataapi.services;

import com.example.afs.flightdataapi.controllers.advice.DataNotFoundException;
import com.example.afs.flightdataapi.model.entities.Ticket;
import com.example.afs.flightdataapi.model.repositories.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    public Ticket findById(String ticketNo) {
        return ticketRepository.findById(ticketNo)
                .orElseThrow(() -> new DataNotFoundException(ticketNo));
    }

    public Ticket save(Ticket ticket) {
        if (ticket.getTicketNo() == null) {
            ticket.setTicketNo(ticketRepository.findMaxTicketNo() + 1L);
        }
        return ticketRepository.save(ticket);
    }

    public Ticket delete(String ticketNo) {
        Ticket ticket = findById(ticketNo);
        ticketRepository.delete(ticket);
        return ticket;
    }
}
