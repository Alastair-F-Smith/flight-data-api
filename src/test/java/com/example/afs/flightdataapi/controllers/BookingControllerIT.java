package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.controllers.advice.DataAccessAdvice;
import com.example.afs.flightdataapi.model.entities.Booking;
import com.example.afs.flightdataapi.testutils.TestConstants;
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

import static org.junit.jupiter.api.Assertions.*;

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

}