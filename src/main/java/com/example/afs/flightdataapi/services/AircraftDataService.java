package com.example.afs.flightdataapi.services;

import com.example.afs.flightdataapi.model.entities.AircraftsData;
import com.example.afs.flightdataapi.model.repositories.AircraftsDataRepository;
import com.example.afs.flightdataapi.model.repositories.SeatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AircraftDataService {

    private final AircraftsDataRepository aircraftsDataRepository;
    private final SeatRepository seatRepository;

    public AircraftDataService(AircraftsDataRepository aircraftsDataRepository, SeatRepository seatRepository) {
        this.aircraftsDataRepository = aircraftsDataRepository;
        this.seatRepository = seatRepository;
    }

    public List<AircraftsData> findAll() {
        return aircraftsDataRepository.findAll();
    }

    public Optional<AircraftsData> findById(String id) {
        return aircraftsDataRepository.findById(id);
    }

    public AircraftsData save(AircraftsData aircraftsData) {
        return aircraftsDataRepository.save(aircraftsData);
    }

    public void deleteById(String id) {
        aircraftsDataRepository.deleteById(id);
        seatRepository.deleteBySeatIdAircraftCode(id);
    }
}
