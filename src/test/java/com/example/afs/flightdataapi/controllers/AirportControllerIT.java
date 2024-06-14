package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.controllers.advice.DataAccessAdvice;
import com.example.afs.flightdataapi.model.dto.AirportDto;
import com.example.afs.flightdataapi.model.dto.Point;
import com.example.afs.flightdataapi.model.entities.Airport;
import com.example.afs.flightdataapi.model.entities.TranslatedField;
import com.example.afs.flightdataapi.model.repositories.AirportRepository;
import com.example.afs.flightdataapi.testutils.TestConstants;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.postgresql.geometric.PGpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Sql(value = TestConstants.POPULATE_SCRIPT_PATH, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AirportControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(TestConstants.POSTGRES_DOCKER_IMAGE)
            .withInitScript(TestConstants.INIT_SCRIPT_PATH);

    WebTestClient webTestClient;

    @Autowired
    DataAccessAdvice dataAccessAdvice;

    @Autowired
    AirportController airportController;

    @Autowired
    AirportRepository airportRepository;

    Airport bryanskAirport;

    @BeforeEach
    void setUp() {
        webTestClient = MockMvcWebTestClient.bindToController(airportController)
                                            .controllerAdvice(dataAccessAdvice)
                                            .build();
        bryanskAirport =new Airport("BZK",
                                    new TranslatedField("Bryansk Airport", "Брянск"),
                                    new TranslatedField("Bryansk", "Брянск"),
                                    new PGpoint(34.1763992309999978, 53.2141990661999955),
                                    TimeZone.getTimeZone("Europe/Moscow"));
    }

    @Test
    @DisplayName("Find all loads all airports from the database")
    void findAllLoadsAllAirportsFromTheDatabase() {
        webTestClient.get()
                .uri("/api/airports/")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(AirportDto.class)
                .hasSize(2);
    }

    @Test
    @DisplayName("Find by ID gets airport with the provided code")
    void findByIdGetsAirportWithTheProvidedCode() {
        webTestClient.get()
                .uri("/api/airports/{code}", "BZK")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(AirportDto.class)
                .isEqualTo(AirportDto.from(bryanskAirport));
    }

    @Test
    @DisplayName("Add new airport adds a new airport to the database")
    void addNewAirportAddsANewAirportToTheDatabase() {
        AirportDto newAirport = new AirportDto("ABC", "Heathrow", "London", new Point(0.0, 0.0), TimeZone.getDefault());

        webTestClient.post()
                .uri("/api/airports")
                .bodyValue(newAirport)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(AirportDto.class)
                .isEqualTo(newAirport);

        assertThat(airportRepository.findById("ABC")).isNotEmpty();
    }

    @Test
    @DisplayName("Update airport updates airport details")
    void updateAirportUpdatesAirportDetails() {
        AirportDto updateAirport = new AirportDto("SGC", "Heathrow", "London", new Point(0.0, 0.0), TimeZone.getDefault());

        webTestClient.put()
                .uri("/api/airports/{code}", "SGC")
                .bodyValue(updateAirport)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(AirportDto.class)
                .isEqualTo(updateAirport);

        Airport updated = airportRepository.findById("SGC").get();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(updated.getAirportName().en()).isEqualTo(updateAirport.name());
            softly.assertThat(updated.getCity().en()).isEqualTo(updateAirport.city());
            softly.assertThat(updated.getCoordinates()).isEqualTo(updateAirport.coordinates().toPgPoint());
            softly.assertThat(updated.getTimezone()).isEqualTo(updateAirport.timeZone());
        });
    }

    @Test
    @DisplayName("Delete airport removes the airport from the database")
    void deleteAirportRemovesTheAirportFromTheDatabase() {
        webTestClient.delete()
                .uri("/api/airports/{code}", "BZK")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(AirportDto.class)
                .isEqualTo(AirportDto.from(bryanskAirport));

        assertThat(airportRepository.count()).isEqualTo(1L);
        assertThat(airportRepository.findById("BZK")).isEmpty();
    }

    @Test
    @DisplayName("Deleting an airport that does not exists returns a 404 status")
    void deletingAnAirportThatDoesNotExistsReturnsA404Status() {
        webTestClient.delete()
                     .uri("/api/airports/{code}", "ABC")
                     .exchange()
                     .expectStatus()
                     .isNotFound();

        assertThat(airportRepository.count()).isEqualTo(2L);
    }

}