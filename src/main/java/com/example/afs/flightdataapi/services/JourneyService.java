package com.example.afs.flightdataapi.services;

import com.example.afs.flightdataapi.controllers.advice.DataNotFoundException;
import com.example.afs.flightdataapi.controllers.advice.FlightAlreadyAddedException;
import com.example.afs.flightdataapi.model.dto.BookingDto;
import com.example.afs.flightdataapi.model.dto.FlightSummaryDto;
import com.example.afs.flightdataapi.model.dto.TicketDto;
import com.example.afs.flightdataapi.model.entities.*;
import com.example.afs.flightdataapi.model.repositories.TicketFlightsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
        List<Ticket> tickets = ticketService.findByBookRef(booking.getBookRef());
        List<FlightSummaryDto> flights = FlightSummaryDto.from(flightService.findByBookRef(booking.getBookRef()));
        booking.setTotalAmount(tickets);
        return BookingDto.from(booking, TicketDto.from(tickets), flights);
    }

    public BookingDto toBookingDto(String bookRef) {
        Booking booking = bookingService.findById(bookRef);
        return toBookingDto(booking);
    }

    public List<Ticket> addFlight(String bookRef, int flightId) {
        if (hasFlight(bookRef, flightId)) {
            throw new FlightAlreadyAddedException(flightId, bookRef);
        }
        Flight flight = flightService.findById(flightId);
        return ticketService.findByBookRef(bookRef)
                            .stream()
                            .map(ticket -> addFlight(ticket, flight, FareConditions.ECONOMY,
                                                     BigDecimal.valueOf(6700)))
                            .toList();
    }

    private boolean hasFlight(String bookRef, int flightId) {
        return flightService.findByBookRef(bookRef)
                            .stream()
                            .anyMatch(flight -> flight.getFlightId() == flightId);
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

    /*
     * Remove a flight from all tickets in a booking. This does not delete the flight or any of the tickets.
     */
    public void removeFlightFromBooking(String bookRef, int flightId) {
        List<Ticket> tickets = ticketService.findByBookRef(bookRef);
        for (var ticket : tickets) {
            TicketFlights ticketFlights = findTicketFlightById(ticket.getTicketNo(), flightId);
            ticket.removeTicketFlight(ticketFlights);
            ticketFlightsRepository.delete(ticketFlights);
            ticketService.save(ticket);
        }
    }

    private TicketFlights findTicketFlightById(String ticketNo, int flightId) {
        TicketFlightsId id = new TicketFlightsId(ticketNo, flightId);
        return ticketFlightsRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(id));
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
