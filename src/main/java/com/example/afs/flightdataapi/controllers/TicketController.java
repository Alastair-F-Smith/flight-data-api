package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.model.entities.Ticket;
import com.example.afs.flightdataapi.services.JourneyService;
import com.example.afs.flightdataapi.services.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TicketController {

    private final JourneyService journeyService;
    private final TicketService ticketService;

    public TicketController(JourneyService journeyService, TicketService ticketService) {
        this.journeyService = journeyService;
        this.ticketService = ticketService;
    }

    @GetMapping("/bookings/{bookRef}/tickets")
    public ResponseEntity<List<Ticket>> getTickets(@PathVariable String bookRef) {
        List<Ticket> tickets = ticketService.findByBookRef(bookRef);
        return ResponseEntity.ok(tickets);
    }

//    @PostMapping("/bookings/{bookRef}/tickets")
//    public ResponseEntity<>
}
