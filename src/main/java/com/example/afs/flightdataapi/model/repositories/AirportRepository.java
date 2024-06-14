package com.example.afs.flightdataapi.model.repositories;

import com.example.afs.flightdataapi.model.entities.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportRepository extends JpaRepository<Airport, String> {

}
