package com.example.afs.flightdataapi.model.repositories;

import com.example.afs.flightdataapi.model.dto.FlightSummaryDto;
import com.example.afs.flightdataapi.model.entities.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Integer> {
    @Query("""
select new com.example.afs.flightdataapi.model.dto.FlightSummaryDto(
    f.flightNo,
    f.scheduledDeparture,
    f.scheduledArrival,
    f.departureAirport.airportCode,
    f.arrivalAirport.airportCode
) from Ticket t
join TicketFlights tf on t.ticketNo = tf.ticketFlightsId.ticketNo
join Flight f on tf.ticketFlightsId.flightId = f.flightId
where t.bookRef.bookRef = :bookRef
""")
    List<FlightSummaryDto> findFlightsByBookRef(String bookRef);
}
