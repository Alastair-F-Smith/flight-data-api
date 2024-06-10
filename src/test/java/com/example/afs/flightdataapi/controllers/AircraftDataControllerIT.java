package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.model.entities.AircraftModel;
import com.example.afs.flightdataapi.model.entities.AircraftsData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Sql(scripts = "classpath:scripts/test-aircrafts-data-populate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AircraftDataControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
                                                    .withInitScript("scripts/init-db.sql");

    WebTestClient webTestClient;

    @Autowired
    AircraftDataController aircraftDataController;

    @Autowired
    ObjectMapper objectMapper;

    AircraftsData aircraft1;
    AircraftsData aircraft2;

    @BeforeEach
    void setup() {
        webTestClient = WebTestClient.bindToController(aircraftDataController).build();
        aircraft1 = new AircraftsData("ABC", new AircraftModel("Boeing", "Boeing"), 10000);
        aircraft2 = new AircraftsData("123", new AircraftModel("Airbus", "Airbus"), 15000);
    }

    @Test
    @DisplayName("Connection to database container established")
    void connectionToDatabaseContainerEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @WithMockUser
    @RepeatedTest(2)
    @DisplayName("Finds all aircraft data")
    void findsAllAircraftData() {
        webTestClient
                .get()
                .uri("/api/aircraft")
                .exchange()
                .expectBodyList(AircraftsData.class)
                .hasSize(1);
    }

    @WithMockUser
    @Test
    @DisplayName("Add aircraft saves the new aircraft data in the database")
    void addAircraftSavesTheNewAircraftDataInTheDatabase() {
        webTestClient
                .post()
                .uri("/api/aircraft")
                .bodyValue(aircraft1)
                .exchange();

        webTestClient
                .get()
                .uri("/api/aircraft")
                .exchange()
                .expectBodyList(AircraftsData.class)
                .hasSize(2);
    }

    @WithMockUser
    @Test
    @DisplayName("Add aircraft returns a response body containing the data that was saved")
    void addAircraftReturnsAResponseBodyContainingTheDataThatWasSaved() {
        webTestClient
                .post()
                .uri("/api/aircraft")
                .bodyValue(aircraft1)
                .exchange()
                .expectBody(AircraftsData.class)
                .isEqualTo(aircraft1);
    }

}