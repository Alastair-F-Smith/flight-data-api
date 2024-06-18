package com.example.afs.flightdataapi.model.repositories;

import com.example.afs.flightdataapi.model.dto.FlightSummaryDto;
import com.example.afs.flightdataapi.model.entities.Flight;
import com.example.afs.flightdataapi.model.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, String> {

    @Query("select max(cast(t.ticketNo as biginteger)) from Ticket t")
    long findMaxTicketNo();

    List<Ticket> findByBookRefBookRef(String bookRef);

    @Query("""
            select t from Ticket t join TicketFlights tf
            on t.ticketNo = tf.ticketFlightsId.ticketNo
            and tf.ticketFlightsId.flightId = :flightId
            """)
    List<Ticket> findByFlightId(int flightId);

    Optional<Ticket> findByTicketNoAndBookRefBookRef(String ticketNo, String bookRef);

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
