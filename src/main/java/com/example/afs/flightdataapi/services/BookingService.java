package com.example.afs.flightdataapi.services;

import com.example.afs.flightdataapi.model.entities.Booking;
import com.example.afs.flightdataapi.model.repositories.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }
}
