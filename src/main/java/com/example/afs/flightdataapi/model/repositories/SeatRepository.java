package com.example.afs.flightdataapi.model.repositories;

import com.example.afs.flightdataapi.model.entities.Seat;
import com.example.afs.flightdataapi.model.entities.SeatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SeatRepository extends JpaRepository<Seat, SeatId> {

    @Modifying
    @Transactional
    @Query(value = "delete from Seat seat where seat.seatId.aircraftCode = :aircraftCode")
    void deleteInBulkByAircraftCode(String aircraftCode);
}
