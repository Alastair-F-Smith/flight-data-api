package com.example.afs.flightdataapi.model.repositories;

import com.example.afs.flightdataapi.model.entities.Airport;
import com.example.afs.flightdataapi.model.entities.TranslatedField;
import com.example.afs.flightdataapi.testutils.TestConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.postgresql.geometric.PGpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@TestPropertySource(properties = {TestConstants.PROPERTIES_DB_REPLACE_NONE})
@Sql(scripts = TestConstants.POPULATE_SCRIPT_PATH)
class AirportRepositoryTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(TestConstants.POSTGRES_DOCKER_IMAGE)
            .withInitScript(TestConstants.INIT_SCRIPT_PATH);

    @Autowired
    AirportRepository airportRepository;

    @Test
    @DisplayName("Find all reads a non-empty list of airport data")
    void findAllReadsANonEmptyListOfAirportData() {
        List<Airport> airports = airportRepository.findAll();
        assertThat(airports).isNotEmpty();
        System.out.println(airports.get(0));
    }

    @Test
    @DisplayName("Save adds valid data to the database")
    void saveAddsValidDataToTheDatabase() {
        TranslatedField name = new TranslatedField("Test", "Test");
        TranslatedField city = new TranslatedField("London", "London");
        PGpoint coords = new PGpoint(0.00, 0.00);
        Airport airport = new Airport("ABC", name, city, coords, TimeZone.getTimeZone("Europe/London"));

        airportRepository.save(airport);

        assertThat(airportRepository.count()).isEqualTo(3L);
    }
}