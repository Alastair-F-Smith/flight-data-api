package com.example.afs.flightdataapi.services;

import com.example.afs.flightdataapi.model.dto.BookingDto;
import com.example.afs.flightdataapi.model.dto.FlightSummaryDto;
import com.example.afs.flightdataapi.model.dto.PersonalDetailsDto;
import com.example.afs.flightdataapi.model.dto.TicketDto;
import com.example.afs.flightdataapi.model.entities.*;
import com.example.afs.flightdataapi.model.repositories.TicketFlightsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class JourneyService {

    private final TicketService ticketService;
    private final FlightService flightService;
    private final BookingService bookingService;
    private final TicketFlightsRepository ticketFlightsRepository;

    public JourneyService(TicketService ticketService, FlightService flightService, BookingService bookingService, TicketFlightsRepository ticketFlightsRepository) {
        this.ticketService = ticketService;
        this.flightService = flightService;
        this.bookingService = bookingService;
        this.ticketFlightsRepository = ticketFlightsRepository;
    }

    public BookingDto toBookingDto(Booking booking) {
        List<TicketDto> people = ticketService.findByBookRef(booking.getBookRef())
                                              .stream()
                                              .map(TicketDto::from)
                                              .toList();
        List<FlightSummaryDto> flights = new ArrayList<>();
        return BookingDto.from(booking, people, flights);
    }

    public BookingDto toBookingDto(String bookRef) {
        Booking booking = bookingService.findById(bookRef);
        return toBookingDto(booking);
    }

    public List<Ticket> addFlight(String bookRef, int flightId) {
        Flight flight = flightService.findById(flightId);
        return ticketService.findByBookRef(bookRef)
                            .stream()
                            .map(ticket -> addFlight(ticket, flight, FareConditions.ECONOMY,
                                                     BigDecimal.valueOf(6700)))
                            .toList();
    }

    public Ticket addFlight(Ticket ticket, Flight flight, FareConditions fareConditions, BigDecimal amount) {
        TicketFlights ticketFlight = new TicketFlights(ticket, flight, fareConditions, amount);
        ticketFlightsRepository.save(ticketFlight);
        ticket.addTicketFlight(ticketFlight);
        return ticketService.save(ticket);
    }

    @Transactional
    public Ticket addFlight(String ticketNo, int flightId, FareConditions fareConditions, BigDecimal amount) {
        Ticket ticket = ticketService.findById(ticketNo);
        Flight flight = flightService.findById(flightId);
        return addFlight(ticket, flight, fareConditions, amount);
    }

    @Transactional
    public Flight deleteFlight(int flightId) {
        Flight flight = flightService.findById(flightId);
        List<Ticket> tickets = ticketService.findByFlightId(flightId);
        ticketService.delete(tickets);
        flightService.delete(flight);
        return flight;
    }
}
