package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.controllers.advice.DataAccessAdvice;
import com.example.afs.flightdataapi.model.entities.Flight;
import com.example.afs.flightdataapi.testutils.TestConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Sql(value = TestConstants.POPULATE_SCRIPT_PATH, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FlightControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(TestConstants.POSTGRES_DOCKER_IMAGE)
            .withInitScript(TestConstants.INIT_SCRIPT_PATH);

    WebTestClient webTestClient;

    @Autowired
    DataAccessAdvice dataAccessAdvice;

    @Autowired
    FlightController flightController;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        webTestClient = MockMvcWebTestClient.bindToController(flightController)
                                            .controllerAdvice(dataAccessAdvice)
                                            .build();
    }

    @Test
    @DisplayName("Find page returns a non-empty page")
    void findPageReturnsANonEmptyPage() {
        webTestClient.get()
                .uri("/api/flights/")
                .exchange()
                .expectBody()
                .jsonPath("$.content").isNotEmpty()
                .jsonPath("$.numberOfElements").isEqualTo(2);
    }

    @Test
    @DisplayName("Find page returns pages of specified size")
    void findPageReturnsPagesOfSpecifiedSize() {
        webTestClient.get()
                     .uri(builder -> builder.path("/api/flights/")
                                            .queryParam("pageSize", 1)
                                            .build())
                     .exchange()
                     .expectBody()
                     .jsonPath("$.content").isNotEmpty()
                     .jsonPath("$.numberOfElements").isEqualTo(1)
                     .jsonPath("$.totalPages").isEqualTo(2);
    }

    @Test
    @DisplayName("Find page returns the requested page number")
    void findPageReturnsTheRequestedPageNumber() {
        webTestClient.get()
                     .uri(builder -> builder.path("/api/flights/")
                                            .queryParam("pageSize", 1)
                                            .queryParam("pageNumber", 1)
                                            .build())
                     .exchange()
                     .expectBody()
                     .jsonPath("$.content").isNotEmpty()
                     .jsonPath("$.last").isEqualTo(true);
    }

    @Test
    @DisplayName("Find pages defaults to sorting results in order of ascending scheduled departure date")
    void findPagesDefaultsToSortingResultsInOrderOfAscendingScheduledDepartureDate() {
        webTestClient.get()
                     .uri("/api/flights/")
                     .exchange()
                     .expectBody()
                     .jsonPath("$.content[0].flightId").isEqualTo(1);
    }

    @Test
    @DisplayName("Find pages returns results sorted in the specified direction")
    void findPagesReturnsResultsSortedInTheSpecifiedDirection() {
        webTestClient.get()
                     .uri(builder -> builder.path("/api/flights/")
                                            .queryParam("sortDirection", "desc")
                                            .build())
                     .exchange()
                     .expectBody()
                     .jsonPath("$.content[0].flightId").isEqualTo(2);
    }

    @Test
    @DisplayName("Find page returns results sorted according to the specified field")
    void findPageReturnsResultsSortedAccordingToTheSpecifiedField() {
        webTestClient.get()
                     .uri(builder -> builder.path("/api/flights/")
                                            .queryParam("sortField", "flightNo")
                                            .build())
                     .exchange()
                     .expectBody()
                     .jsonPath("$.content[0].flightId").isEqualTo(2);
    }

}