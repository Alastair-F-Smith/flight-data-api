package com.example.afs.flightdataapi.model.repositories;

import com.example.afs.flightdataapi.model.entities.TicketFlights;
import com.example.afs.flightdataapi.model.entities.TicketFlightsId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketFlightsRepository extends JpaRepository<TicketFlights, TicketFlightsId> {
}
