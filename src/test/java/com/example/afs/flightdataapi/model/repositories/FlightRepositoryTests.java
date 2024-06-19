package com.example.afs.flightdataapi.model.repositories;

import com.example.afs.flightdataapi.model.dto.FlightSummaryDto;
import com.example.afs.flightdataapi.model.entities.AircraftsData;
import com.example.afs.flightdataapi.model.entities.Airport;
import com.example.afs.flightdataapi.model.entities.Flight;
import com.example.afs.flightdataapi.testutils.TestConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@DataJpaTest
@Testcontainers
@TestPropertySource(properties = {TestConstants.PROPERTIES_DB_REPLACE_NONE})
@Sql(scripts = TestConstants.POPULATE_SCRIPT_PATH)
class FlightRepositoryTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(TestConstants.POSTGRES_DOCKER_IMAGE)
            .withInitScript(TestConstants.INIT_SCRIPT_PATH);

    @Autowired
    FlightRepository flightRepository;

    @Autowired
    AirportRepository airportRepository;

    @Autowired
    AircraftsDataRepository aircraftsDataRepository;

    @Test
    @DisplayName("Can load a non-empty list of flights from the database")
    void canLoadANonEmptyListOfFlightsFromTheDatabase() {
        List<Flight> flights = flightRepository.findAll();
        assertThat(flights).isNotEmpty();
        System.out.println(flights.getFirst());
    }

    @Test
    @DisplayName("Can save new flight data")
    void canSaveNewFlightData() {
        ZonedDateTime now = ZonedDateTime.now();
        Airport arrivalAirport = airportRepository.findById("SGC").get();
        Airport departureAirport = airportRepository.findById("BZK").get();
        AircraftsData aircraft = aircraftsDataRepository.findById("773").get();

        Flight flight = new Flight("AB1234", now, now.plusHours(2), departureAirport, arrivalAirport, "Arrived", aircraft, now, now.plusHours(2));

        Flight saved = flightRepository.save(flight);

        assertThat(flightRepository.count()).isEqualTo(3L);
        assertThat(flightRepository.findById(saved.getFlightId())).isNotEmpty();

    }

    @Test
    @DisplayName("Returns a non-empty list of flight summary data for a given booking ref")
    void returnsANonEmptyListOfFlightSummaryDataForAGivenBookingRef() {
        List<FlightSummaryDto> flightSummaries = flightRepository.findFlightsByBookRef("00044D");
        System.out.println(flightSummaries);
        assertThat(flightSummaries).isNotEmpty();
    }

}