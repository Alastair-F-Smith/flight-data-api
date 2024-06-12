package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.controllers.advice.DataAccessAdvice;
import com.example.afs.flightdataapi.controllers.advice.ErrorResponse;
import com.example.afs.flightdataapi.model.entities.Seat;
import com.example.afs.flightdataapi.testutils.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.fail;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Sql(value = TestConstants.POPULATE_SCRIPT_PATH, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class SeatControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(TestConstants.POSTGRES_DOCKER_IMAGE)
            .withInitScript(TestConstants.INIT_SCRIPT_PATH);


    @Autowired
    SeatController seatController;

    @Autowired
    DataAccessAdvice dataAccessAdvice;

    @Autowired
    WebTestClient webTestClient;

    final String AIRCRAFT_CODE = "773";

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(seatController)
                                     .controllerAdvice(dataAccessAdvice)
                                     .build();
    }

    @Nested
    @DisplayName("Get all by aircraft")
    class getAllByAircraft {

        @WithMockUser
        @Test
        @DisplayName("Get all by aircraft returns the correct number of seats")
        void getAllByAircraftReturnsTheCorrectNumberOfSeats() {
            webTestClient.get()
                         .uri("/api/aircraft/{id}/seats", AIRCRAFT_CODE)
                         .exchange()
                         .expectBodyList(Seat.class)
                         .hasSize(2);
        }

        @WithMockUser
        @Test
        @DisplayName("Get all by aircraft returns a response with the correct status code")
        void getAllByAircraftReturnsAResponseWithTheCorrectStatusCode() {
            webTestClient.get()
                         .uri("/api/aircraft/{id}/seats", AIRCRAFT_CODE)
                         .exchange()
                         .expectStatus()
                         .isOk()
                         .expectHeader()
                         .contentType(MediaType.APPLICATION_JSON);
        }

        @WithMockUser
        @Test
        @DisplayName("Get all by aircraft returns a not found status code if the aircraft does not exist")
        void getAllByAircraftReturnsANotFoundStatusCodeIfTheAircraftDoesNotExist() {
            webTestClient.get()
                         .uri("/api/aircraft/{id}/seats", "XXX")
                         .exchange()
                         .expectStatus()
                         .isNotFound()
                         .expectHeader()
                         .contentType(MediaType.APPLICATION_JSON);
        }

        @WithMockUser
        @Test
        @DisplayName("Get all by aircraft returns a response with an informative error message if the aircraft does not exist")
        void getAllByAircraftReturnsAResponseWithAnInformativeErrorMessageIfTheAircraftDoesNotExist() {
            webTestClient.get()
                         .uri("/api/aircraft/{id}/seats", "XXX")
                         .exchange()
                         .expectBody(ErrorResponse.class)
                         .value(body -> assertThat(body.message(), containsString("XXX")));
        }
    }


}