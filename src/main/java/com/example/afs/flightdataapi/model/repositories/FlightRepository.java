package com.example.afs.flightdataapi.model.repositories;

import com.example.afs.flightdataapi.model.entities.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, Integer> {
}
