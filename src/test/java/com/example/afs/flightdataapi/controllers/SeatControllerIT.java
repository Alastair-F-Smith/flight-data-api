package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.controllers.advice.DataAccessAdvice;
import com.example.afs.flightdataapi.controllers.advice.ErrorResponse;
import com.example.afs.flightdataapi.model.entities.FareConditions;
import com.example.afs.flightdataapi.model.entities.Seat;
import com.example.afs.flightdataapi.model.entities.SeatId;
import com.example.afs.flightdataapi.testutils.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
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

        @Test
        @DisplayName("Get all by aircraft returns the correct number of seats")
        void getAllByAircraftReturnsTheCorrectNumberOfSeats() {
            webTestClient.get()
                         .uri("/api/aircraft/{id}/seats", AIRCRAFT_CODE)
                         .exchange()
                         .expectBodyList(Seat.class)
                         .hasSize(2);
        }

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

    @Nested
    @DisplayName("Get specific seat")
    class getSpecificSeat {

        @Test
        @DisplayName("Get seat returns the correct seat data")
        void getSeatReturnsTheCorrectSeatData() {
            webTestClient.get()
                    .uri("/api/aircraft/{id}/seats/{seatNo}", AIRCRAFT_CODE, "43G")
                    .exchange()
                    .expectBody(Seat.class)
                    .value(seat -> assertThat(seat.getFareConditions(), is(FareConditions.ECONOMY)));
        }

        @Test
        @DisplayName("Get seat returns a 200 status code")
        void getSeatReturnsA200StatusCode() {
            webTestClient.get()
                         .uri("/api/aircraft/{id}/seats/{seatNo}", AIRCRAFT_CODE, "43G")
                         .exchange()
                         .expectStatus()
                         .isOk()
                         .expectHeader()
                         .contentType(MediaType.APPLICATION_JSON);
        }

        @Test
        @DisplayName("Get seat returns a 404 status if aircraft is not found")
        void getSeatReturnsA404StatusIfAircraftIsNotFound() {
            webTestClient.get()
                         .uri("/api/aircraft/{id}/seats/{seatNo}", "XXX", "43G")
                         .exchange()
                         .expectStatus()
                         .isNotFound()
                         .expectHeader()
                         .contentType(MediaType.APPLICATION_JSON);
        }

        @Test
        @DisplayName("Get seat returns a 404 status if the seat is not found")
        void getSeatReturnsA404StatusIfTheSeatIsNotFound() {
            webTestClient.get()
                         .uri("/api/aircraft/{id}/seats/{seatNo}", AIRCRAFT_CODE, "XXX")
                         .exchange()
                         .expectStatus()
                         .isNotFound()
                         .expectHeader()
                         .contentType(MediaType.APPLICATION_JSON);
        }

        @Test
        @DisplayName("Get seat returns an error response with a message containing the seat id not found")
        void getSeatReturnsAnErrorResponseWithAMessageContainingTheSeatIdNotFound() {
            SeatId expectedId = new SeatId(AIRCRAFT_CODE, "XXX");

            webTestClient.get()
                         .uri("/api/aircraft/{id}/seats/{seatNo}", AIRCRAFT_CODE, "XXX")
                         .exchange()
                    .expectBody(ErrorResponse.class)
                    .value(response -> assertThat(response.message(), containsString(expectedId.toString())));
        }

    }


}