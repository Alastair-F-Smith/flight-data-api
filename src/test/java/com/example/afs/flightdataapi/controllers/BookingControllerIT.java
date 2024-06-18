package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.controllers.advice.DataAccessAdvice;
import com.example.afs.flightdataapi.model.dto.BookingDto;
import com.example.afs.flightdataapi.model.dto.PersonalDetailsDto;
import com.example.afs.flightdataapi.model.entities.Booking;
import com.example.afs.flightdataapi.model.repositories.BookingRepository;
import com.example.afs.flightdataapi.model.repositories.TicketRepository;
import com.example.afs.flightdataapi.testutils.TestConstants;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Sql(value = TestConstants.POPULATE_SCRIPT_PATH, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class BookingControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(TestConstants.POSTGRES_DOCKER_IMAGE)
            .withInitScript(TestConstants.INIT_SCRIPT_PATH);

    WebTestClient webTestClient;

    @Autowired
    DataAccessAdvice dataAccessAdvice;

    @Autowired
    BookingController bookingController;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    TicketRepository ticketRepository;

    @BeforeEach
    void setUp() {
        webTestClient = MockMvcWebTestClient.bindToController(bookingController)
                .controllerAdvice(dataAccessAdvice)
                .build();
    }

    @Test
    @DisplayName("Get all returns a list of all bookings in the database")
    void getAllReturnsAListOfAllBookingsInTheDatabase() {
        webTestClient.get()
                .uri("/api/bookings")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Booking.class)
                .hasSize(2);
    }

    @Test
    @DisplayName("Get by reference returns the booking with the provided reference")
    void getByReferenceReturnsTheBookingWithTheProvidedReference() {
        String bookRef = "00044D";
        webTestClient.get()
                .uri("/api/bookings/{bookRef}", bookRef)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BookingDto.class)
                .value(booking -> assertThat(booking.bookRef()).isEqualTo(bookRef));
    }

    @Test
    @DisplayName("Create booking correctly saves the booking to the database")
    void createBookingCorrectlySavesTheBookingToTheDatabase() {
        webTestClient.post()
                .uri("/api/bookings")
                .exchange();

        assertThat(bookingRepository.count()).isEqualTo(3);
    }

    @Test
    @DisplayName("Create booking returns a 201 response with the newly created booking details")
    void createBookingReturnsA201ResponseWithTheNewlyCreatedBookingDetails() {
        ZonedDateTime now = ZonedDateTime.now();
        webTestClient.post()
                .uri("/api/bookings")
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(BookingDto.class)
                .value(booking -> SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(booking.bookRef()).isNotEmpty();
                    softly.assertThat(booking.bookDate()).isBetween(now.minusMinutes(1), now.plusMinutes(1));
                    softly.assertThat(booking.totalAmount()).isEqualTo(BigDecimal.ZERO);
                }));
    }

    @Test
    @DisplayName("Add person returns booking details with the new person added")
    void addPersonReturnsBookingDetailsWithTheNewPersonAdded() {
        PersonalDetailsDto person = new PersonalDetailsDto("John", "john@email.com", "12345678");
        String bookRef = "00044D";
        webTestClient.post()
                .uri("/api/bookings/{bookRef}", bookRef)
                .bodyValue(person)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(BookingDto.class)
                .value(booking -> assertThat(booking.people()).contains(person));
    }

    @Test
    @DisplayName("Add person results in a new ticket being stored")
    void addPersonResultsInANewTicketBeingStored() {
        PersonalDetailsDto person = new PersonalDetailsDto("John", "john@email.com", "12345678");
        String bookRef = "00044D";
        webTestClient.post()
                     .uri("/api/bookings/{bookRef}", bookRef)
                     .bodyValue(person)
                     .exchange();

        assertThat(ticketRepository.count()).isEqualTo(3);
    }

    @Test
    @DisplayName("Cancel booking removes the booking from the database")
    void cancelBookingRemovesTheBookingFromTheDatabase() {
        String bookRef = "00044D";
        webTestClient.delete()
                .uri("/api/bookings/{bookRef}", bookRef)
                .exchange();

        assertThat(bookingRepository.count()).isEqualTo(1);
        assertThat(bookingRepository.findById(bookRef)).isEmpty();
    }

}