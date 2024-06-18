package com.example.afs.flightdataapi.model.repositories;

import com.example.afs.flightdataapi.model.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

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
}
