package com.example.afs.flightdataapi.services;

import com.example.afs.flightdataapi.controllers.advice.DataNotFoundException;
import com.example.afs.flightdataapi.model.entities.Booking;
import com.example.afs.flightdataapi.model.repositories.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TicketService ticketService;
    private final Logger logger = LoggerFactory.getLogger(BookingService.class);

    public BookingService(BookingRepository bookingRepository, TicketService ticketService) {
        this.bookingRepository = bookingRepository;
        this.ticketService = ticketService;
    }

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    public Booking findById(String bookRef) {
        logger.debug("Search for booking with reference {}...", bookRef);
        return bookingRepository.findById(bookRef)
                .orElseThrow(() -> new DataNotFoundException(bookRef));
    }

    public boolean contains(String bookRef) {
        return bookingRepository.findById(bookRef).isPresent();
    }

    public Booking create() {
        Booking newBooking = new Booking();
        while (contains(newBooking.getBookRef())) {
            newBooking.setBookRef(Booking.generateBookRef());
        }
        logger.debug("Saving new booking with reference {}", newBooking.getBookRef());
        return save(newBooking);
    }

    public Booking save(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking delete(String bookRef) {
        Booking booking = findById(bookRef);
        logger.debug("Deleting associated tickets...");
        ticketService.deleteByBookRef(bookRef);
        logger.debug("Deleting booking {}...", bookRef);
        bookingRepository.delete(booking);
        logger.debug("Booking deleted successfully.");
        return booking;
    }
}
