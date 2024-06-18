package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.model.dto.BookingDto;
import com.example.afs.flightdataapi.model.dto.PersonalDetailsDto;
import com.example.afs.flightdataapi.model.entities.Booking;
import com.example.afs.flightdataapi.model.entities.Ticket;
import com.example.afs.flightdataapi.services.BookingService;
import com.example.afs.flightdataapi.services.JourneyService;
import com.example.afs.flightdataapi.services.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BookingController {

    private final BookingService bookingService;
    private final TicketService ticketService;
    private final JourneyService journeyService;

    public BookingController(BookingService bookingService, TicketService ticketService, JourneyService journeyService) {
        this.bookingService = bookingService;
        this.ticketService = ticketService;
        this.journeyService = journeyService;
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.findAll();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/bookings/{bookRef}")
    public ResponseEntity<BookingDto> getBookingByRef(@PathVariable String bookRef) {
        Booking booking = bookingService.findById(bookRef);
        return ResponseEntity.ok(journeyService.toBookingDto(booking));
    }

    @GetMapping("/bookings/{bookRef}/tickets/{ticketNo}")
    public ResponseEntity<Ticket> getTicket(@PathVariable String bookRef, @PathVariable String ticketNo) {
        Ticket ticket = ticketService.findById(ticketNo, bookRef);
        return ResponseEntity.ok(ticket);
    }

    /*
     * Create a new booking. This generates a valid reference number and sets other
     * fields to default values. The booking returned in the response can be used
     * to begin adding tickets.
     */
    @PostMapping("/bookings")
    public ResponseEntity<BookingDto> createBooking() {
        Booking saved = bookingService.create();
        URI uri = bookingUri(saved.getBookRef());
        return ResponseEntity.created(uri)
                .body(BookingDto.from(saved));
    }

    private URI bookingUri(String bookRef) {
        return UriComponentsBuilder.fromHttpUrl("http://localhost:8080/api")
                                   .pathSegment("bookings", bookRef)
                                   .build().toUri();
    }

    @PostMapping("/bookings/{bookRef}")
    public ResponseEntity<BookingDto> addPerson(@PathVariable String bookRef,
                                                @RequestBody PersonalDetailsDto person) {
        Booking booking = bookingService.findById(bookRef);
        ticketService.save(booking, person);
        BookingDto bookingDto = journeyService.toBookingDto(booking);
        return ResponseEntity.created(bookingUri(bookRef))
                             .body(bookingDto);
    }

    @DeleteMapping("/bookings/{bookRef}/tickets/{ticketNo}")
    public ResponseEntity<BookingDto> removePerson(@PathVariable String bookRef, @PathVariable String ticketNo) {
        Ticket ticket = ticketService.findById(ticketNo, bookRef);
        ticketService.delete(ticket);
        BookingDto bookingDto = journeyService.toBookingDto(bookRef);
        return ResponseEntity.ok(bookingDto);
    }

    @DeleteMapping("/bookings/{bookRef}")
    public ResponseEntity<Booking> cancelBooking(@PathVariable String bookRef) {
        Booking cancelled = bookingService.delete(bookRef);
        return ResponseEntity.ok(cancelled);
    }
}
