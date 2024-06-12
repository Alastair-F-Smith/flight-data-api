package com.example.afs.flightdataapi.services;

import com.example.afs.flightdataapi.controllers.advice.DataNotFoundException;
import com.example.afs.flightdataapi.model.entities.AircraftsData;
import com.example.afs.flightdataapi.model.entities.Seat;
import com.example.afs.flightdataapi.model.entities.SeatId;
import com.example.afs.flightdataapi.model.repositories.SeatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeatService {

    private final SeatRepository seatRepository;
    private final AircraftDataService aircraftDataService;

    public SeatService(SeatRepository seatRepository, AircraftDataService aircraftDataService) {
        this.seatRepository = seatRepository;
        this.aircraftDataService = aircraftDataService;
    }

    public List<Seat> findAll() {
        return seatRepository.findAll();
    }

    public List<Seat> findAllByAircraftCode(String aircraftCode) {
        aircraftDataService.findById(aircraftCode)
                           .orElseThrow(() -> new DataNotFoundException(aircraftCode));
        return seatRepository.findBySeatIdAircraftCode(aircraftCode);
    }

    public Seat findById(String aircraftCode, String seatNo) {
        SeatId seatId = new SeatId(aircraftCode, seatNo);
        return findById(seatId);
    }

    public Seat findById(SeatId seatId) {
        return seatRepository.findById(seatId)
                             .orElseThrow(() -> new DataNotFoundException(seatId));
    }

    public Seat save(Seat seat) {
        return seatRepository.save(seat);
    }

    public void deleteById(String aircraftCode, String seatNo) {
        Seat seat = findById(aircraftCode, seatNo);
        seatRepository.delete(seat);
    }
}
