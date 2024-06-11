package com.example.afs.flightdataapi.services;

import com.example.afs.flightdataapi.model.repositories.SeatRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@TestPropertySource(properties = {"spring.test.database.replace=none"})
@Import({AircraftDataService.class})
@Sql(scripts = "classpath:scripts/test-aircrafts-data-populate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AircraftDataServiceTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withInitScript("scripts/init-db.sql");

    @Autowired
    AircraftDataService aircraftDataService;

    @Autowired
    SeatRepository seatRepository;

    @Test
    @DisplayName("Deleting an aircraft by ID removes associated seats")
    void deletingAnAircraftByIdRemovesAssociatedSeats() {
        long initialSeats = seatRepository.count();
        aircraftDataService.deleteById("773");
        assertThat(seatRepository.count()).isLessThan(initialSeats);
    }

}