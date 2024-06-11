package com.example.afs.flightdataapi.model.repositories;

import com.example.afs.flightdataapi.model.entities.AircraftsData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AircraftsDataRepository extends JpaRepository<AircraftsData, String> {
}
