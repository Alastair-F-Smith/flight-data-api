package com.example.afs.flightdataapi.services;

import com.example.afs.flightdataapi.controllers.advice.DataNotFoundException;
import com.example.afs.flightdataapi.model.dto.PersonalDetailsDto;
import com.example.afs.flightdataapi.model.entities.Booking;
import com.example.afs.flightdataapi.model.entities.Ticket;
import com.example.afs.flightdataapi.model.repositories.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
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

    public Ticket findById(String ticketNo, String bookRef) {
        return ticketRepository.findByTicketNoAndBookRefBookRef(ticketNo, bookRef)
                .orElseThrow(() -> new DataNotFoundException(ticketNo));
    }

    public List<Ticket> findByFlightId(int flightId) {
        return ticketRepository.findByFlightId(flightId);
    }

    public List<Ticket> findByBookRef(String bookRef) {
        return ticketRepository.findByBookRefBookRef(bookRef);
    }

    @Transactional
    public Ticket save(Booking booking, PersonalDetailsDto person) {
        Ticket ticket = new Ticket(booking, person.name(), person.contactDetails());
        return save(ticket);
    }

    @Transactional
    public Ticket save(Ticket ticket) {
        if (ticket.getTicketNo() == null) {
            ticket.setTicketNo(ticketRepository.findMaxTicketNo() + 1L);
        }
        return ticketRepository.save(ticket);
    }

    public Ticket update(String ticketNo, String bookRef, PersonalDetailsDto details) {
        Ticket ticket  = findById(ticketNo, bookRef);
        return update(ticket, details);
    }

    public Ticket update(Ticket ticket, PersonalDetailsDto details) {
        ticket.updateDetails(details);
        return save(ticket);
    }

    @Transactional
    public List<Ticket> deleteByBookRef(String bookRef) {
        List<Ticket> tickets = findByBookRef(bookRef);
        delete(tickets);
        return tickets;
    }

    public Ticket delete(String ticketNo) {
        Ticket ticket = findById(ticketNo);
        ticketRepository.delete(ticket);
        return ticket;
    }

    public void delete(Ticket ticket) {
        ticketRepository.delete(ticket);
    }

    public void delete(List<Ticket> tickets) {
        for (var ticket : tickets) {
            delete(ticket);
        }
    }
}
