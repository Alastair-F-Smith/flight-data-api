package com.example.afs.flightdataapi.model.repositories;

import com.example.afs.flightdataapi.model.entities.Booking;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@DataJpaTest
@Testcontainers
@TestPropertySource(properties = {TestConstants.PROPERTIES_DB_REPLACE_NONE})
@Sql(scripts = TestConstants.POPULATE_SCRIPT_PATH)
class BookingRepositoryTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(TestConstants.POSTGRES_DOCKER_IMAGE)
            .withInitScript(TestConstants.INIT_SCRIPT_PATH);

    @Autowired
    BookingRepository bookingRepository;

    @Test
    @DisplayName("Find all reads a non-empty list of booking data")
    void findAllReadsANonEmptyListOfBookingData() {
        List<Booking> bookings = bookingRepository.findAll();
        assertThat(bookings).isNotEmpty();
        System.out.println(bookings.get(0));
    }

    @Test
    @DisplayName("New bookings can be saved to the repository")
    void newBookingsCanBeSavedToTheRepository() {
        Booking booking = new Booking("12345F", ZonedDateTime.now(), BigDecimal.valueOf(10_000L));
        bookingRepository.save(booking);
        assertThat(bookingRepository.count()).isEqualTo(3L);
    }

}