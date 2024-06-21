package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.controllers.advice.DataAccessAdvice;
import com.example.afs.flightdataapi.controllers.advice.ErrorResponse;
import com.example.afs.flightdataapi.model.dto.BookingDto;
import com.example.afs.flightdataapi.model.dto.PersonalDetailsDto;
import com.example.afs.flightdataapi.model.dto.TicketDto;
import com.example.afs.flightdataapi.model.entities.Booking;
import com.example.afs.flightdataapi.model.entities.FareConditions;
import com.example.afs.flightdataapi.model.repositories.BookingRepository;
import com.example.afs.flightdataapi.model.repositories.TicketFlightsRepository;
import com.example.afs.flightdataapi.model.repositories.TicketRepository;
import com.example.afs.flightdataapi.testutils.TestConstants;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withinPercentage;
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

    @Autowired
    TicketFlightsRepository ticketFlightsRepository;

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
    @DisplayName("Get by reference returns the booking with passenger details populated")
    void getByReferenceReturnsTheBookingWithPassengerAndFlightDetailsPopulated() {
        String bookRef = "00044D";
        webTestClient.get()
                     .uri("/api/bookings/{bookRef}", bookRef)
                     .exchange()
                     .expectStatus()
                     .isOk()
                     .expectBody(BookingDto.class)
                     .value(booking -> assertThat(booking.people()).isNotEmpty());
    }

    @Test
    @DisplayName("Get by reference returns the booking with flight details populated")
    void getByReferenceReturnsTheBookingWithFlightDetailsPopulated() {
        String bookRef = "00044D";
        webTestClient.get()
                     .uri("/api/bookings/{bookRef}", bookRef)
                     .exchange()
                     .expectStatus()
                     .isOk()
                     .expectBody(BookingDto.class)
                     .value(booking -> assertThat(booking.flights()).isNotEmpty());
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
                     .value(booking -> assertBookingContainsPerson(booking, person))
                .value(booking -> assertThat(booking.people()).hasSize(2));
    }

    private void assertBookingContainsPerson(BookingDto booking, PersonalDetailsDto person) {
        assertThat(booking.people())
                .anyMatch(ticket -> person.equals(ticket.details()));
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
    @DisplayName("Add flight returns a response with the new flight details added to the booking")
    void addFlightReturnsAResponseWithTheNewFlightDetailsAddedToTheBooking() {
        int flightId = 2;
        String bookRef = "00044D";
        webTestClient.post()
                     .uri("/api/bookings/{bookRef}/flights/{flightId}", bookRef, flightId)
                     .exchange()
                     .expectBody(BookingDto.class)
                     .value(booking -> assertThat(booking.flights()).hasSize(2));
    }

    @Test
    @DisplayName("Add flight correctly updates the total amount of the booking")
    void addFlightCorrectlyUpdatesTheTotalAmountOfTheBooking() {
        int flightId = 2;
        String bookRef = "00044D";
        webTestClient.post()
                     .uri("/api/bookings/{bookRef}/flights/{flightId}", bookRef, flightId)
                     .exchange()
                     .expectBody(BookingDto.class)
                     .value(booking -> assertThat(booking.totalAmount()).isCloseTo(BigDecimal.valueOf(61960), withinPercentage(1)));
    }

    @Test
    @DisplayName("Attempting to add the same flight to a booking twice will return a 400 response")
    void attemptingToAddTheSameFlightToABookingTwiceWillReturnA400Response() {
        int flightId = 1;
        String bookRef = "00044D";
        webTestClient.post()
                     .uri("/api/bookings/{bookRef}/flights/{flightId}", bookRef, flightId)
                     .exchange()
                     .expectStatus()
                     .isBadRequest()
                     .expectBody(ErrorResponse.class)
                     .value(response -> assertThat(response.message()).contains(String.valueOf(flightId), bookRef));
    }

    @Test
    @DisplayName("The correct amount is added to the booking total when a fare condition is provided")
    void theCorrectAmountIsAddedToTheBookingTotalWhenAFareConditionIsProvided() {
        int flightId = 2;
        String bookRef = "00044D";
        webTestClient.post()
                     .uri(builder -> builder.pathSegment("api", "bookings", bookRef, "flights", String.valueOf(flightId))
                                            .queryParam("fareConditions", "BUSINESS")
                                            .build())
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectBody(BookingDto.class)
                     .value(booking -> assertThat(booking.totalAmount()).isCloseTo(BigDecimal.valueOf(84200), withinPercentage(1)));
    }

    @Test
    @DisplayName("Update details changes the personal details on a ticket")
    void updateDetailsChangesThePersonalDetailsOnATicket() {
        String bookRef = "000374";
        String ticketNo = "0005435990692";
        PersonalDetailsDto person = new PersonalDetailsDto("John", "john@email.com", "12345678");
        TicketDto ticket = new TicketDto(ticketNo, person);

        webTestClient.patch()
                .uri("/api/bookings/{bookRef}/tickets/{ticketNo}", bookRef, ticketNo)
                .bodyValue(person)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BookingDto.class)
                .value(booking -> assertThat(booking.people()).contains(ticket))
                .value(booking -> assertThat(booking.people()).hasSize(1));
    }

    @Test
    @DisplayName("Remove person removes the person from a booking")
    void removePersonRemovesThePersonFromABooking() {
        String bookRef = "000374";
        String ticketNo = "0005435990692";

        webTestClient.delete()
                .uri("/api/bookings/{bookRef}/tickets/{ticketNo}", bookRef, ticketNo)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BookingDto.class)
                .value(booking -> assertThat(booking.people()).isEmpty());
    }

    @Test
    @DisplayName("Remove person removes a ticket from the database")
    void removePersonRemovesATicketFromTheDatabase() {
        String bookRef = "000374";
        String ticketNo = "0005435990692";

        webTestClient.delete()
                     .uri("/api/bookings/{bookRef}/tickets/{ticketNo}", bookRef, ticketNo)
                     .exchange();

        assertThat(ticketRepository.count()).isEqualTo(1);
        assertThat(ticketRepository.findById(ticketNo)).isEmpty();
    }

    @Test
    @DisplayName("Remove person returns a 404 status when the ticket and booking ref do not match")
    void removePersonReturnsA404StatusWhenTheTicketAndBookingRefDoNotMatch() {
        String bookRef = "00044D";
        String ticketNo = "0005435990692";

        webTestClient.delete()
                     .uri("/api/bookings/{bookRef}/tickets/{ticketNo}", bookRef, ticketNo)
                     .exchange()
                     .expectStatus()
                     .isNotFound();
    }

    @Test
    @DisplayName("Remove flight removes the specified flight from a booking")
    void removeFlightRemovesTheSpecifiedFlightFromABooking() {
        int flightId = 1;
        String bookRef = "00044D";

        webTestClient.delete()
                     .uri("/api/bookings/{bookRef}/flights/{flightId}", bookRef, flightId)
                     .exchange()
                     .expectStatus()
                     .isOk()
                     .expectBody(BookingDto.class)
                     .value(booking -> assertThat(booking.flights()).isEmpty());
    }

    @Test
    @DisplayName("Attempting to remove a flight that is not on a booking returns a 404 not found response")
    void attemptingToRemoveAFlightThatIsNotOnABookingReturnsA404NotFoundResponse() {
        int flightId = 2;
        String bookRef = "00044D";

        webTestClient.delete()
                     .uri("/api/bookings/{bookRef}/flights/{flightId}", bookRef, flightId)
                     .exchange()
                     .expectStatus()
                     .isNotFound();
    }

    @Test
    @DisplayName("Removing a flight from a ticket deletes the record from the ticket flights join table")
    void removingAFlightFromATicketDeletesTheRecordFromTheTicketFlightsJoinTable() {
        int flightId = 1;
        String bookRef = "00044D";
        long initialCount = ticketFlightsRepository.count();

        webTestClient.delete()
                     .uri("/api/bookings/{bookRef}/flights/{flightId}", bookRef, flightId)
                     .exchange();

        assertThat(ticketFlightsRepository.count()).isLessThan(initialCount);
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