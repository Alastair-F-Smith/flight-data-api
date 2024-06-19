package com.example.afs.flightdataapi.model.repositories;

import com.example.afs.flightdataapi.model.dto.FlightSummaryDto;
import com.example.afs.flightdataapi.model.entities.*;
import com.example.afs.flightdataapi.testutils.TestConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@DataJpaTest
@Testcontainers
@TestPropertySource(properties = {TestConstants.PROPERTIES_DB_REPLACE_NONE})
@Sql(scripts = TestConstants.POPULATE_SCRIPT_PATH)
class TicketRepositoryTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(TestConstants.POSTGRES_DOCKER_IMAGE)
            .withInitScript(TestConstants.INIT_SCRIPT_PATH);

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    FlightRepository flightRepository;

    @Autowired
    TicketFlightsRepository ticketFlightsRepository;

    @Test
    @DisplayName("Can retrieve a non-empty list of tickets from the repository")
    void canRetrieveANonEmptyListOfTicketsFromTheRepository() {
        List<Ticket> tickets = ticketRepository.findAll();
        assertThat(tickets).isNotEmpty();
        System.out.println(tickets);
    }

    @Test
    @DisplayName("Can add a new ticket to the repository")
    void canAddANewTicketToTheRepository() {
        Booking booking = new Booking("00055A", ZonedDateTime.now(), BigDecimal.valueOf(5_000));
        bookingRepository.save(booking);
        ContactData contactData = new ContactData("alice.jones@email.com", "+448080123456");
        Ticket ticket = new Ticket("0000123456789", booking, "1001 123456", "Alice Jones", contactData);
        ticketRepository.save(ticket);

        assertThat(ticketRepository.count()).isEqualTo(3);
        assertThat(ticketRepository.findById("0000123456789")).isNotEmpty();
    }

    @Test
    @DisplayName("Can associate a ticket with a flight and save it to the repository")
    void canAssociateATicketWithAFlightAndSaveItToTheRepository() {
        Booking booking = new Booking("00055A", ZonedDateTime.now(), BigDecimal.valueOf(5_000));
        bookingRepository.save(booking);
        ContactData contactData = new ContactData("alice.jones@email.com", "+448080123456");
        Flight flight = flightRepository.findById(1).get();

        // Save ticket with no associated flight data
        Ticket ticket = new Ticket("0000123456789", booking, "1001 123456", "Alice Jones", contactData);
        Ticket saved = ticketRepository.save(ticket);

        TicketFlights ticketFlight = new TicketFlights(ticket, flight, FareConditions.COMFORT, BigDecimal.valueOf(9000));

        // Update the ticket with the ticket-flight data
        ticket.setTicketFlights(List.of(ticketFlight));
        Ticket updated = ticketRepository.save(ticket);

        assertThat(ticketRepository.count()).isEqualTo(3);
        assertThat(ticketRepository.findById("0000123456789")).isNotEmpty();
        assertThat(saved.getTicketFlights()).isNotEmpty();
    }

    @Test
    @DisplayName("Can find the maximum ticket number")
    void canFindTheMaximumTicketNumber() {
        long maxTicketNo = ticketRepository.findMaxTicketNo();
        assertThat(maxTicketNo).isEqualTo(5435990693L);
    }

    @Test
    @DisplayName("Returns a non-empty list when searching for a valid flight ID")
    void returnsANonEmptyListWhenSearchingForAValidFlightId() {
        List<Ticket> tickets = ticketRepository.findByFlightId(2);
        assertThat(tickets).isNotEmpty();
    }

    @Test
    @DisplayName("Returns an empty list when search for a flight ID that doesn't exist")
    void returnsAnEmptyListWhenSearchForAFlightIdThatDoesnTExist() {
        List<Ticket> tickets = ticketRepository.findByFlightId(3);
        assertThat(tickets).isEmpty();
    }

}