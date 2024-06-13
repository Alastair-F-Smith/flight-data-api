package com.example.afs.flightdataapi.services;

import com.example.afs.flightdataapi.model.repositories.SeatRepository;
import com.example.afs.flightdataapi.testutils.TestConstants;
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
@TestPropertySource(properties = {TestConstants.PROPERTIES_DB_REPLACE_NONE})
@Import({AircraftDataService.class})
@Sql(scripts = TestConstants.POPULATE_SCRIPT_PATH, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AircraftDataServiceTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(TestConstants.POSTGRES_DOCKER_IMAGE)
            .withInitScript(TestConstants.INIT_SCRIPT_PATH);

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