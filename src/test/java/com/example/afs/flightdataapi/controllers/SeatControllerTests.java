package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.config.SecurityConfig;
import com.example.afs.flightdataapi.controllers.advice.DataAccessAdvice;
import com.example.afs.flightdataapi.controllers.advice.ErrorResponse;
import com.example.afs.flightdataapi.model.dto.SeatDto;
import com.example.afs.flightdataapi.model.entities.FareConditions;
import com.example.afs.flightdataapi.services.SeatService;
import com.example.afs.flightdataapi.services.TokenService;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@WebMvcTest({SeatController.class, DataAccessAdvice.class, AuthController.class})
@Import({TokenService.class, SecurityConfig.class})
class SeatControllerTests {

    WebTestClient webTestClient;

    @Autowired
    SeatController seatController;

    @Autowired
    DataAccessAdvice dataAccessAdvice;

    @MockBean
    SeatService seatService;

    @BeforeEach
    void setUp() {
        webTestClient = MockMvcWebTestClient.bindToController(seatController)
                .controllerAdvice(dataAccessAdvice)
                .build();
    }

    @Nested
    @DisplayName("Add seat field validation")
    class addSeatFieldValidation {

        @Test
        @DisplayName("When provided seatNo is too long, return an error response with the appropriate message")
        void whenProvidedSeatNoIsTooLongReturnAnErrorResponseWithTheAppropriateMessage() throws Exception {
            SeatDto invalidSeat = new SeatDto("ABC", "12345", FareConditions.BUSINESS);

            webTestClient.post()
                         .uri("/api/aircraft/{id}/seats", "ABC")
                         .bodyValue(invalidSeat)
                         .exchange()
                         .expectBody(ErrorResponse.class)
                         .value(response -> SoftAssertions.assertSoftly(softly -> {
                                                                            softly.assertThat(response.message())
                                                                                  .contains("seatNo: rejected value '12345'");
                                                                            softly.assertThat(response.statusCode())
                                                                                  .isEqualTo(400);
                                                                            softly.assertThat(response.reason())
                                                                                  .contains(SeatDto.SEAT_NO_LENGTH_MESSAGE);
                                                                        }
                         ));
        }

        @Test
        @DisplayName("The provided seat number cannot be null")
        void theProvidedSeatNumberCannotBeNull() {
            SeatDto invalidSeatNo = new SeatDto("ABC", null, FareConditions.BUSINESS);

            String uri = UriComponentsBuilder.fromUriString("/api/aircraft/{id}/seats").build("ABC").toString();

            webTestClient.post()
                         .uri(uri)
                         .bodyValue(invalidSeatNo)
                         .exchange()
                         .expectBody(ErrorResponse.class)
                         .value(response -> SoftAssertions.assertSoftly(softly -> {
                                                                            softly.assertThat(response.message())
                                                                                  .contains("seatNo: rejected value 'null'");
                                                                            softly.assertThat(response.statusCode())
                                                                                  .isEqualTo(400);
                                                                            softly.assertThat(response.reason())
                                                                                  .contains(SeatDto.SEAT_NO_NULL_MESSAGE);
                                                                            softly.assertThat(response.request())
                                                                                    .isEqualTo(uri);
                                                                        }
                         ));
        }

        @Test
        @DisplayName("The provided seat number cannot be blank")
        void theProvidedSeatNumberCannotBeBlank() {
            SeatDto invalidSeatNo = new SeatDto("ABC", "  ", FareConditions.BUSINESS);

            webTestClient.post()
                         .uri("/api/aircraft/{id}/seats", "ABC")
                         .bodyValue(invalidSeatNo)
                         .exchange()
                         .expectBody(ErrorResponse.class)
                         .value(response -> SoftAssertions.assertSoftly(softly -> {
                                                                            softly.assertThat(response.message())
                                                                                  .contains("seatNo: rejected value '  '");
                                                                            softly.assertThat(response.statusCode())
                                                                                  .isEqualTo(400);
                                                                            softly.assertThat(response.reason())
                                                                                  .contains(SeatDto.SEAT_NO_NULL_MESSAGE);
                                                                        }
                         ));
        }

        @Test
        @DisplayName("Seat number must start with a digit")
        void seatNumberMustStartWithADigit() {
            SeatDto invalidSeatNo = new SeatDto("ABC", "A12", FareConditions.BUSINESS);

            webTestClient.post()
                         .uri("/api/aircraft/{id}/seats", "ABC")
                         .bodyValue(invalidSeatNo)
                         .exchange()
                         .expectBody(ErrorResponse.class)
                         .value(response -> SoftAssertions.assertSoftly(softly -> {
                                                                            softly.assertThat(response.message())
                                                                                  .contains("seatNo: rejected value 'A12'");
                                                                            softly.assertThat(response.statusCode())
                                                                                  .isEqualTo(400);
                                                                            softly.assertThat(response.reason())
                                                                                  .contains(SeatDto.SEAT_NO_STARTS_WITH_DIGIT);
                                                                        }
                         ));
        }

        @Test
        @DisplayName("Aircraft code cannot be less than 3 characters long")
        void aircraftCodeCannotBeLessThan3CharactersLong() {
            SeatDto invalidCode = new SeatDto("AB", "12A", FareConditions.BUSINESS);

            webTestClient.post()
                         .uri("/api/aircraft/{id}/seats", "AB")
                         .bodyValue(invalidCode)
                         .exchange()
                         .expectBody(ErrorResponse.class)
                         .value(response -> SoftAssertions.assertSoftly(softly -> {
                                                                            softly.assertThat(response.message())
                                                                                  .contains("aircraftCode: rejected value 'AB'");
                                                                            softly.assertThat(response.statusCode())
                                                                                  .isEqualTo(400);
                                                                            softly.assertThat(response.reason())
                                                                                  .contains(SeatDto.AIRCRAFT_CODE_LENGTH_MESSAGE);
                                                                        }
                         ));
        }

        @Test
        @DisplayName("Aircraft code cannot be more than 3 characters long")
        void aircraftCodeCannotBeMoreThan3CharactersLong() {
            SeatDto invalidCode = new SeatDto("ABCD", "12A", FareConditions.BUSINESS);

            webTestClient.post()
                         .uri("/api/aircraft/{id}/seats", "ABCD")
                         .bodyValue(invalidCode)
                         .exchange()
                         .expectBody(ErrorResponse.class)
                         .value(response -> SoftAssertions.assertSoftly(softly -> {
                                                                            softly.assertThat(response.message())
                                                                                  .contains("aircraftCode: rejected value 'ABCD'");
                                                                            softly.assertThat(response.statusCode())
                                                                                  .isEqualTo(400);
                                                                            softly.assertThat(response.reason())
                                                                                  .contains(SeatDto.AIRCRAFT_CODE_LENGTH_MESSAGE);
                                                                        }
                         ));
        }

        @Test
        @DisplayName("The provided aircraft code cannot be null")
        void theProvidedAircraftCodeCannotBeNull() {
            SeatDto invalidCode = new SeatDto(null, "12A", FareConditions.BUSINESS);

            webTestClient.post()
                         .uri("/api/aircraft/{id}/seats", "ABC")
                         .bodyValue(invalidCode)
                         .exchange()
                         .expectBody(ErrorResponse.class)
                         .value(response -> SoftAssertions.assertSoftly(softly -> {
                                                                            softly.assertThat(response.message())
                                                                                  .contains("aircraftCode: rejected value 'null'");
                                                                            softly.assertThat(response.statusCode())
                                                                                  .isEqualTo(400);
                                                                            softly.assertThat(response.reason())
                                                                                  .contains(SeatDto.AIRCRAFT_CODE_NULL_MESSAGE);
                                                                        }
                         ));
        }

        @Test
        @DisplayName("The provided aircraft code cannot be blank")
        void theProvidedAircraftCodeCannotBeBlank() {
            SeatDto invalidCode = new SeatDto("   ", "12A", FareConditions.BUSINESS);

            webTestClient.post()
                         .uri("/api/aircraft/{id}/seats", "ABC")
                         .bodyValue(invalidCode)
                         .exchange()
                         .expectBody(ErrorResponse.class)
                         .value(response -> SoftAssertions.assertSoftly(softly -> {
                                                                            softly.assertThat(response.message())
                                                                                  .contains("aircraftCode: rejected value '   '");
                                                                            softly.assertThat(response.statusCode())
                                                                                  .isEqualTo(400);
                                                                            softly.assertThat(response.reason())
                                                                                  .contains(SeatDto.AIRCRAFT_CODE_NULL_MESSAGE);
                                                                        }
                         ));
        }

        @Test
        @DisplayName("Aircraft code in the path parameter and request body must match")
        void aircraftCodeInThePathParameterAndRequestBodyMustMatch() {
            SeatDto seat = new SeatDto("XYZ", "12A", FareConditions.BUSINESS);

            String expectedMessage = "Mismatch between identifier provided in path (ABC) and in the request body (XYZ)";

            webTestClient.post()
                         .uri("/api/aircraft/{id}/seats", "ABC")
                         .bodyValue(seat)
                         .exchange()
                         .expectBody(ErrorResponse.class)
                         .value(response -> SoftAssertions.assertSoftly(softly -> {
                                                                            softly.assertThat(response.message())
                                                                                  .contains(expectedMessage);
                                                                            softly.assertThat(response.statusCode())
                                                                                  .isEqualTo(400);
                                                                        }
                         ));
        }

    }

}