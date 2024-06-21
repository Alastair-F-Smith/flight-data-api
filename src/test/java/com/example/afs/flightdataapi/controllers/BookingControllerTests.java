package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.controllers.advice.DataAccessAdvice;
import com.example.afs.flightdataapi.controllers.advice.ErrorResponse;
import com.example.afs.flightdataapi.controllers.advice.NotValidatedMessages;
import com.example.afs.flightdataapi.model.dto.PersonalDetailsDto;
import com.example.afs.flightdataapi.services.BookingService;
import com.example.afs.flightdataapi.services.JourneyService;
import com.example.afs.flightdataapi.services.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest({BookingController.class, DataAccessAdvice.class})
class BookingControllerTests {

    WebTestClient webTestClient;

    @Autowired
    BookingController bookingController;

    @Autowired
    DataAccessAdvice dataAccessAdvice;

    @MockBean
    BookingService bookingService;
    @MockBean
    TicketService ticketService;
    @MockBean
    JourneyService journeyService;

    String bookRef = "000374";

    @BeforeEach
    void setup() {
        webTestClient = MockMvcWebTestClient.bindToController(bookingController)
                                            .controllerAdvice(dataAccessAdvice)
                                            .build();
    }

    @Nested
    @DisplayName("Add person validation")
    class AddPersonValidation {

        @Test
        @DisplayName("Valid passenger details are accepted")
        void validPassengerDetailsAreAccepted() {
            PersonalDetailsDto person = new PersonalDetailsDto("John Smith", "john@email.com", "+808012345678");
            webTestClient.post()
                         .uri("/api/bookings/{bookRef}", bookRef)
                         .bodyValue(person)
                         .exchange()
                         .expectStatus()
                         .isCreated();
        }

        @Test
        @DisplayName("Passenger name cannot be blank")
        void passengerNameCannotBeBlank() {
            PersonalDetailsDto person = new PersonalDetailsDto(" ", "john@email.com", "+808012345678");
            webTestClient.post()
                    .uri("/api/bookings/{bookRef}", bookRef)
                    .bodyValue(person)
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(response -> assertThat(response.reason()).contains(NotValidatedMessages.TEXT_FIELD_REQUIRED));
        }

        @Test
        @DisplayName("Emails must be valid")
        void emailsMustBeValid() {
            PersonalDetailsDto person = new PersonalDetailsDto("John Smith", "john.email.com", "+808012345678");
            webTestClient.post()
                         .uri("/api/bookings/{bookRef}", bookRef)
                         .bodyValue(person)
                         .exchange()
                         .expectStatus()
                         .isBadRequest()
                         .expectBody(ErrorResponse.class)
                         .value(response -> assertThat(response.reason()).contains(NotValidatedMessages.INVALID_EMAIL));
        }

        @Test
        @DisplayName("Emails can be missing")
        void emailsCanBeMissing() {
            PersonalDetailsDto person = new PersonalDetailsDto("John Smith", null, "+808012345678");
            webTestClient.post()
                         .uri("/api/bookings/{bookRef}", bookRef)
                         .bodyValue(person)
                         .exchange()
                         .expectStatus()
                         .isCreated();
        }

        @Test
        @DisplayName("Phone numbers without an initial plus are valid")
        void phoneNumbersWithoutAnInitialPlusAreValid() {
            PersonalDetailsDto person = new PersonalDetailsDto("John Smith", "john@email.com", "808012345678");
            webTestClient.post()
                         .uri("/api/bookings/{bookRef}", bookRef)
                         .bodyValue(person)
                         .exchange()
                         .expectStatus()
                         .isCreated();
        }

        @Test
        @DisplayName("Phone numbers can be missing")
        void phoneNumbersCanBeMissing() {
            PersonalDetailsDto person = new PersonalDetailsDto("John Smith", "john@email.com", null);
            webTestClient.post()
                         .uri("/api/bookings/{bookRef}", bookRef)
                         .bodyValue(person)
                         .exchange()
                         .expectStatus()
                         .isCreated();
        }

        @Test
        @DisplayName("Phone numbers cannot contain more than one plus symbol")
        void phoneNumbersCannotContainMoreThanOnePlusSymbol() {
            PersonalDetailsDto person = new PersonalDetailsDto("John Smith", "john@email.com", "++808012345678");
            webTestClient.post()
                         .uri("/api/bookings/{bookRef}", bookRef)
                         .bodyValue(person)
                         .exchange()
                         .expectStatus()
                         .isBadRequest()
                         .expectBody(ErrorResponse.class)
                         .value(response -> assertThat(response.reason()).contains(NotValidatedMessages.INVALID_PHONE_NUMBER));
        }

        @Test
        @DisplayName("Phone numbers cannot contain spaces")
        void phoneNumbersCannotContainSpaces() {
            PersonalDetailsDto person = new PersonalDetailsDto("John Smith", "john@email.com", "+8080 12345678");
            webTestClient.post()
                         .uri("/api/bookings/{bookRef}", bookRef)
                         .bodyValue(person)
                         .exchange()
                         .expectStatus()
                         .isBadRequest()
                         .expectBody(ErrorResponse.class)
                         .value(response -> assertThat(response.reason()).contains(NotValidatedMessages.INVALID_PHONE_NUMBER));
        }

    }

    @Nested
    @DisplayName("Edit person validation")
    class EditPersonValidation {

        @Test
        @DisplayName("A name must be provided")
        void aNameMustBeProvided() {
            PersonalDetailsDto person = new PersonalDetailsDto(" ", "john@email.com", "+808012345678");
            webTestClient.patch()
                         .uri("/api/bookings/{bookRef}/tickets/{ticketNo}", bookRef, "0005435990692")
                         .bodyValue(person)
                         .exchange()
                         .expectStatus()
                         .isBadRequest()
                         .expectBody(ErrorResponse.class)
                         .value(response -> assertThat(response.reason()).contains(NotValidatedMessages.TEXT_FIELD_REQUIRED));
        }

    }
}