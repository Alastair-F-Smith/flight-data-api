package com.example.afs.flightdataapi.services;

import com.example.afs.flightdataapi.controllers.advice.DataNotFoundException;
import com.example.afs.flightdataapi.model.dto.SeatDto;
import com.example.afs.flightdataapi.model.entities.AircraftsData;
import com.example.afs.flightdataapi.model.entities.Seat;
import com.example.afs.flightdataapi.model.entities.SeatId;
import com.example.afs.flightdataapi.model.repositories.SeatRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeatService {

    private final SeatRepository seatRepository;
    private final AircraftDataService aircraftDataService;
    private final Logger logger = LoggerFactory.getLogger(SeatService.class);

    public SeatService(SeatRepository seatRepository, AircraftDataService aircraftDataService) {
        this.seatRepository = seatRepository;
        this.aircraftDataService = aircraftDataService;
    }

    public Seat fromDto(SeatDto seatDto) {
        AircraftsData aircraft = aircraftDataService.findById(seatDto.aircraftCode())
                .orElseThrow(() -> new DataNotFoundException(seatDto.aircraftCode()));
        logger.debug("Found aircraft data for {}, generating seat entity", aircraft.getAircraftCode());
        return new Seat(seatDto.seatId(), aircraft, seatDto.fareConditions());
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

    public Seat update(SeatDto seatDto) {
        Seat seat = findById(seatDto.seatId());
        seat.setFareConditions(seatDto.fareConditions());
        return save(seat);
    }

    public Seat deleteById(String aircraftCode, String seatNo) {
        Seat seat = findById(aircraftCode, seatNo);
        seatRepository.delete(seat);
        return seat;
    }
}
