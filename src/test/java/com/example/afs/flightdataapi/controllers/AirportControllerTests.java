package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.controllers.advice.DataAccessAdvice;
import com.example.afs.flightdataapi.controllers.advice.ErrorResponse;
import com.example.afs.flightdataapi.model.dto.AirportDto;
import com.example.afs.flightdataapi.model.dto.Point;
import com.example.afs.flightdataapi.services.AirportService;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest({AirportController.class, DataAccessAdvice.class})
class AirportControllerTests {

    WebTestClient webTestClient;

    @Autowired
    AirportController airportController;

    @Autowired
    DataAccessAdvice dataAccessAdvice;

    @MockBean
    AirportService airportService;

    @BeforeEach
    void setUp() {
        webTestClient = MockMvcWebTestClient.bindToController(airportController)
                                            .controllerAdvice(dataAccessAdvice)
                                            .build();
    }

    @Nested
    @DisplayName("Add airport validation")
    class addAirportValidation {

        @ParameterizedTest
        @ValueSource(strings = {"AB", "ABCD"})
        @DisplayName("The airport code must be exactly 3 characters long")
        void theAirportCodeCannotBeLessThan3CharactersLong(String airportCode) {
            AirportDto shortCode = new AirportDto(airportCode, "London", "Heathrow", new Point(0.0, 0.0), TimeZone.getDefault());

            webTestClient.post()
                    .uri("/api/airports")
                    .bodyValue(shortCode)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(response -> assertThat(response.reason()).contains(AirportDto.AIRPORT_CODE_LENGTH_MESSAGE));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "  "})
        @DisplayName("The airport code cannot be blank or null")
        void theAirportCodeCannotBeBlankOrNull(String airportCode) {
            AirportDto shortCode = new AirportDto(airportCode, "London", "Heathrow", new Point(0.0, 0.0), TimeZone.getDefault());

            webTestClient.post()
                         .uri("/api/airports")
                         .bodyValue(shortCode)
                         .exchange()
                         .expectStatus()
                         .isBadRequest()
                         .expectBody(ErrorResponse.class)
                         .value(response -> assertThat(response.reason()).contains(AirportDto.AIRPORT_CODE_NULL_MESSAGE));
        }

        @ParameterizedTest
        @ValueSource(ints = {-181, 181})
        @DisplayName("Airport longitude must be in the range -180 to 180")
        void airportLatitudeMustBeInTheRange180To180(int lon) {
            AirportDto shortCode = new AirportDto("ABC", "London", "Heathrow", new Point(lon, 0.0), TimeZone.getDefault());

            webTestClient.post()
                         .uri("/api/airports")
                         .bodyValue(shortCode)
                         .exchange()
                         .expectStatus()
                         .isBadRequest()
                         .expectBody(ErrorResponse.class)
                         .value(response -> assertThat(response.reason()).contains(Point.LONGITUDE_RANGE_MESSAGE));
        }

        @ParameterizedTest
        @ValueSource(ints = {-91, 91})
        @DisplayName("Airport latitude must be in the range -90 to 90")
        void airportLatitudeMustBeInTheRange90To90(int lat) {
            AirportDto shortCode = new AirportDto("ABC", "London", "Heathrow", new Point(0.0, lat), TimeZone.getDefault());

            webTestClient.post()
                         .uri("/api/airports")
                         .bodyValue(shortCode)
                         .exchange()
                         .expectStatus()
                         .isBadRequest()
                         .expectBody(ErrorResponse.class)
                         .value(response -> assertThat(response.reason()).contains(Point.LATITUDE_RANGE_MESSAGE));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "  ", "\t"})
        @DisplayName("Airport name cannot be blank")
        void airportNameCannotBeBlank(String name) {
            AirportDto shortCode = new AirportDto("ABC", name, "Heathrow", new Point(0.0, 0.0), TimeZone.getDefault());

            webTestClient.post()
                         .uri("/api/airports")
                         .bodyValue(shortCode)
                         .exchange()
                         .expectStatus()
                         .isBadRequest()
                         .expectBody(ErrorResponse.class)
                         .value(response -> assertThat(response.reason()).contains(AirportDto.AIRPORT_NAME_MESSAGE));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "  ", "\t"})
        @DisplayName("City name cannot be blank")
        void cityNameCannotBeBlank(String city) {
            AirportDto shortCode = new AirportDto("ABC", "London", city, new Point(0.0, 0.0), TimeZone.getDefault());

            webTestClient.post()
                         .uri("/api/airports")
                         .bodyValue(shortCode)
                         .exchange()
                         .expectStatus()
                         .isBadRequest()
                         .expectBody(ErrorResponse.class)
                         .value(response -> assertThat(response.reason()).contains(AirportDto.CITY_BLANK_MESSAGE));
        }

        @Test
        @DisplayName("Time zone cannot be null")
        void timeZoneCannotBeNull() {
            AirportDto shortCode = new AirportDto("ABC", "Heathrow", "London", new Point(0.0, 0.0), null);

            webTestClient.post()
                         .uri("/api/airports")
                         .bodyValue(shortCode)
                         .exchange()
                         .expectStatus()
                         .isBadRequest()
                         .expectBody(ErrorResponse.class)
                         .value(response -> assertThat(response.reason()).contains(AirportDto.TIMEZONE_NULL_MESSAGE));
        }

    }


}