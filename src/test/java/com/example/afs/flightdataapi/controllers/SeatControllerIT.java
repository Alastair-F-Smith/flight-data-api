package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.controllers.advice.DataAccessAdvice;
import com.example.afs.flightdataapi.controllers.advice.ErrorResponse;
import com.example.afs.flightdataapi.model.dto.SeatDto;
import com.example.afs.flightdataapi.model.entities.FareConditions;
import com.example.afs.flightdataapi.model.entities.Seat;
import com.example.afs.flightdataapi.model.entities.SeatId;
import com.example.afs.flightdataapi.model.repositories.SeatRepository;
import com.example.afs.flightdataapi.services.SeatService;
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
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

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
    SeatRepository seatRepository;

    @Autowired
    WebTestClient webTestClient;

    final String AIRCRAFT_CODE = "773";

    @BeforeEach
    void setUp() {
        webTestClient = MockMvcWebTestClient.bindToController(seatController)
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
                         .expectBodyList(SeatDto.class)
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
                         .value(body -> assertThat(body.message()).contains("XXX"));
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
                    .expectBody(SeatDto.class)
                    .value(seat -> assertThat(seat.fareConditions()).isEqualTo(FareConditions.ECONOMY));
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
                    .value(response -> assertThat(response.message()).contains((expectedId.toString())));
        }

    }

    @Nested
    @DisplayName("Add seat")
    class addSeat {

        @Test
        @DisplayName("Adds the seat to the database")
        void addsTheSeatToTheDatabase() {
            SeatDto newSeat = new SeatDto(AIRCRAFT_CODE, "1A", FareConditions.BUSINESS);

            webTestClient.post()
                    .uri("/api/aircraft/{id}/seats", AIRCRAFT_CODE)
                    .bodyValue(newSeat)
                    .exchange();

            Optional<Seat> addedSeat = seatRepository.findById(newSeat.seatId());
            assertThat(addedSeat).isNotEmpty();
        }

        @Test
        @DisplayName("On success, returns a 201 status code")
        void onSuccessReturnsA201StatusCode() {
            SeatDto newSeat = new SeatDto(AIRCRAFT_CODE, "1A", FareConditions.BUSINESS);

            webTestClient.post()
                         .uri("/api/aircraft/{id}/seats", AIRCRAFT_CODE)
                         .bodyValue(newSeat)
                         .exchange()
                         .expectStatus()
                         .isCreated();
        }

        @Test
        @DisplayName("On success, returns the added seat data in the response body")
        void onSuccessReturnsTheAddedSeatDataInTheResponseBody() {
            SeatDto newSeat = new SeatDto(AIRCRAFT_CODE, "1A", FareConditions.BUSINESS);

            webTestClient.post()
                         .uri("/api/aircraft/{id}/seats", AIRCRAFT_CODE)
                         .bodyValue(newSeat)
                         .exchange()
                         .expectBody(SeatDto.class)
                         .isEqualTo(newSeat);
        }

        @Test
        @DisplayName("If aircraft is not found, returns a 404 status code")
        void ifAircraftIsNotFoundReturnsA404StatusCode() {
            String invalidCode = "XXX";
            SeatDto newSeat = new SeatDto(invalidCode, "1A", FareConditions.BUSINESS);

            webTestClient.post()
                         .uri("/api/aircraft/{id}/seats", invalidCode)
                         .bodyValue(newSeat)
                         .exchange()
                         .expectStatus()
                         .isNotFound();
        }

        @Test
        @DisplayName("If the provided data is invalid, returns a 400 status code")
        void ifTheProvidedDataIsInvalidReturnsA400StatusCode() {
            String invalidNumber = "12345";
            SeatDto newSeat = new SeatDto(AIRCRAFT_CODE, invalidNumber, FareConditions.BUSINESS);

            webTestClient.post()
                         .uri("/api/aircraft/{id}/seats", AIRCRAFT_CODE)
                         .bodyValue(newSeat)
                         .exchange()
                         .expectStatus()
                         .isBadRequest();
        }

    }

    @Nested
    @DisplayName("Delete seat")
    class deleteSeat {

        @Test
        @DisplayName("Removes deleted seat from the database")
        void removesDeletedSeatFromTheDatabase() {
            String aircraftCode = "773";
            String seatNo = "43G";

            webTestClient.delete()
                    .uri("/api/aircraft/{id}/seats/{seatNo}", aircraftCode, seatNo)
                    .exchange()
                    .expectStatus()
                    .isOk();

            assertThat(seatRepository.findById(new SeatId(aircraftCode, seatNo))).isEmpty();
        }

        @Test
        @DisplayName("Returns details of the deleted seat in the response body")
        void returnsDetailsOfTheDeletedSeatInTheResponseBody() {
            String aircraftCode = "773";
            String seatNo = "43G";

            Seat deleted = seatRepository.findById(new SeatId(aircraftCode, seatNo))
                                         .get();

            webTestClient.delete()
                         .uri("/api/aircraft/{id}/seats/{seatNo}", aircraftCode, seatNo)
                         .exchange()
                         .expectBody(SeatDto.class)
                         .isEqualTo(SeatDto.from(deleted));
        }

        @Test
        @DisplayName("Returns a 404 status if the seat does not exist")
        void returnsA404StatusIfTheSeatDoesNotExist() {
            String aircraftCode = "773";
            String seatNo = "100G";

            webTestClient.delete()
                         .uri("/api/aircraft/{id}/seats/{seatNo}", aircraftCode, seatNo)
                         .exchange()
                         .expectStatus()
                         .isNotFound();
        }

    }


}