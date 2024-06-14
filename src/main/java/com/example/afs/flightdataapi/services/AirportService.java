package com.example.afs.flightdataapi.services;

import com.example.afs.flightdataapi.controllers.advice.DataNotFoundException;
import com.example.afs.flightdataapi.model.dto.AirportDto;
import com.example.afs.flightdataapi.model.entities.Airport;
import com.example.afs.flightdataapi.model.repositories.AirportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AirportService {

    public final AirportRepository airportRepository;

    public AirportService(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    public List<Airport> findAll() {
        return airportRepository.findAll();
    }

    public Airport findById(String airportCode) {
        return airportRepository.findById(airportCode)
                .orElseThrow(() -> new DataNotFoundException(airportCode));
    }

    public Airport save(Airport airport) {
        return airportRepository.save(airport);
    }

    public Airport delete(String airportCode) {
        Airport airport = findById(airportCode);
        airportRepository.delete(airport);
        return airport;
    }

    public Airport fromDto(AirportDto airportDto) {
        Airport saved = airportRepository.findById(airportDto.airportCode())
                .orElse(new Airport());

        saved.setAirportCode(airportDto.airportCode());
        saved.setCoordinates(airportDto.coordinates()
                                       .toPgPoint());
        saved.setAirportName(airportDto.name());
        saved.setCity(airportDto.city());
        saved.setTimezone(airportDto.timeZone());
        return saved;
    }
}
