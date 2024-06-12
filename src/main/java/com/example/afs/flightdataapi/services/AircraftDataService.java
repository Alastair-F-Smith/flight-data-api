package com.example.afs.flightdataapi.services;

import com.example.afs.flightdataapi.model.entities.AircraftsData;
import com.example.afs.flightdataapi.model.repositories.AircraftsDataRepository;
import com.example.afs.flightdataapi.model.repositories.SeatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AircraftDataService {

    private final AircraftsDataRepository aircraftsDataRepository;
    private final SeatRepository seatRepository;
    private final Logger logger = LoggerFactory.getLogger(AircraftDataService.class);

    public AircraftDataService(AircraftsDataRepository aircraftsDataRepository, SeatRepository seatRepository) {
        this.aircraftsDataRepository = aircraftsDataRepository;
        this.seatRepository = seatRepository;
    }

    public List<AircraftsData> findAll() {
        return aircraftsDataRepository.findAll();
    }

    public Optional<AircraftsData> findById(String id) {
        logger.debug("Searching for aircraft data with id {}...", id);
        var found = aircraftsDataRepository.findById(id);
        logger.debug("Result of search: {}", found.isPresent() ? "Found!" : "Not found.");
        return aircraftsDataRepository.findById(id);
    }

    public AircraftsData save(AircraftsData aircraftsData) {
        return aircraftsDataRepository.save(aircraftsData);
    }

    @Transactional
    public void deleteById(String id) {
        logger.debug("Attempting to delete aircraft data with id {}...", id);
        aircraftsDataRepository.deleteById(id);
        logger.debug("Removing associated seat data...");
        seatRepository.deleteInBulkByAircraftCode(id);
    }
}
