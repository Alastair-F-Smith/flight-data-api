package com.example.afs.flightdataapi.model.repositories;

import com.example.afs.flightdataapi.model.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, String> {
}
