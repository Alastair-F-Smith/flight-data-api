package com.example.afs.flightdataapi.model.repositories;

import com.example.afs.flightdataapi.model.entities.FareConditions;
import com.example.afs.flightdataapi.model.entities.Seat;
import com.example.afs.flightdataapi.model.entities.SeatId;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@DataJpaTest
@Testcontainers
@TestPropertySource(properties = {TestConstants.PROPERTIES_DB_REPLACE_NONE})
@Sql(scripts = TestConstants.POPULATE_SCRIPT_PATH, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class SeatRepositoryTests {

    @Autowired
    SeatRepository seatRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(TestConstants.POSTGRES_DOCKER_IMAGE)
            .withInitScript(TestConstants.INIT_SCRIPT_PATH);

    @Test
    @DisplayName("Find all returns a list of seats")
    void findAllReturnsAListOfSeats() {
        List<Seat> seats = seatRepository.findAll();
        assertThat(seats).hasSize(2);
    }

    @Test
    @DisplayName("Find by id returns seat data when a valid id is provided")
    void findByIdReturnsSeatDataWhenAValidIdIsProvided() {
        SeatId seatId = new SeatId("773", "43G");
        Optional<Seat> seatOptional = seatRepository.findById(seatId);
        assertThat(seatOptional).isNotEmpty();
        Seat seat = seatOptional.get();
        assertThat(seat.getFareConditions()).isEqualTo(FareConditions.ECONOMY);
    }

}