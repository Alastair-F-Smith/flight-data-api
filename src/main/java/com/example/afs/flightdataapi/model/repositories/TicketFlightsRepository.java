package com.example.afs.flightdataapi.model.repositories;

import com.example.afs.flightdataapi.model.entities.TicketFlights;
import com.example.afs.flightdataapi.model.entities.TicketFlightsId;
import com.example.afs.flightdataapi.services.pricing.FareAmounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketFlightsRepository extends JpaRepository<TicketFlights, TicketFlightsId> {

    @Query("""
        select new com.example.afs.flightdataapi.services.pricing.FareAmounts(
        tf.ticketFlightsId.flightId, tf.fareConditions, min(tf.amount))
        from TicketFlights tf
        group by tf.ticketFlightsId.flightId, tf.fareConditions
        """)
    List<FareAmounts> findFareAmounts();

    @Query("""
        select new com.example.afs.flightdataapi.services.pricing.FareAmounts(
        0, tf.fareConditions, avg(tf.amount)
        ) from TicketFlights tf
        group by tf.fareConditions
        """)
    List<FareAmounts> findAverageFareAmountByFareConditions();

}
