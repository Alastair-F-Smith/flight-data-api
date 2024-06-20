package com.example.afs.flightdataapi.model.repositories;

import com.example.afs.flightdataapi.model.dto.FlightSummaryDto;
import com.example.afs.flightdataapi.model.entities.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Integer>, JpaSpecificationExecutor<Flight> {
    @Query("""
select f from Ticket t
join TicketFlights tf on t.ticketNo = tf.ticketFlightsId.ticketNo
join Flight f on tf.ticketFlightsId.flightId = f.flightId
where t.bookRef.bookRef = :bookRef
""")
    List<Flight> findFlightsByBookRef(String bookRef);

    Page<Flight> findByScheduledDeparture(ZonedDateTime departureTime, Pageable pageable);

    List<Flight> findByAircraftCode_AircraftCode(String aircraftCode);
}
