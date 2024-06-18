package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.model.entities.Booking;
import com.example.afs.flightdataapi.services.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.findAll();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/bookings/{bookRef}")
    public ResponseEntity<Booking> getBookingByRef(@PathVariable String bookRef) {
        Booking booking = bookingService.findById(bookRef);
        return ResponseEntity.ok(booking);
    }

    /*
     * Create a new booking. This generates a valid reference number and sets other
     * fields to default values. The booking returned in the response can be used
     * to begin adding tickets.
     */
    @PostMapping("/bookings")
    public ResponseEntity<Booking> createBooking() {
        Booking saved = bookingService.create();
        URI uri = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/api")
                                      .pathSegment("bookings", saved.getBookRef())
                                      .build().toUri();
        return ResponseEntity.created(uri)
                .body(saved);
    }

    @DeleteMapping("/bookings/{bookRef}")
    public ResponseEntity<Booking> cancelBooking(@PathVariable String bookRef) {
        Booking cancelled = bookingService.delete(bookRef);
        return ResponseEntity.ok(cancelled);
    }
}
