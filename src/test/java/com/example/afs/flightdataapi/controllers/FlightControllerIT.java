package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.controllers.advice.DataAccessAdvice;
import com.example.afs.flightdataapi.testutils.TestConstants;
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

    @Test
    @DisplayName("Search with no parameters returns all flights")
    void searchWithNoParametersReturnsAllFlights() {
        webTestClient.get()
                     .uri(builder -> builder.path("/api/flights/search")
                                            .build())
                     .exchange()
                     .expectStatus()
                     .isOk()
                     .expectBody()
                     .jsonPath("$.content.length()").isEqualTo(2);
    }

    @Test
    @DisplayName("Search correctly filters by departure time")
    void searchCorrectlyFiltersByDepartureTime() {
        webTestClient.get()
                     .uri(builder -> builder.path("/api/flights/search")
                                            .queryParam("departureTime", "2017-07-16T00:00Z")
                                            .build())
                     .exchange()
                     .expectStatus()
                     .isOk()
                     .expectBody()
                     .jsonPath("$.content[0].flightId").isEqualTo(1)
                     .jsonPath("$.content.length()").isEqualTo(1);
    }

    @Test
    @DisplayName("Search returns an empty response if criteria do not match any records")
    void searchReturnsAnEmptyResponseIfCriteriaDoNotMatchAnyRecords() {
        webTestClient.get()
                     .uri(builder -> builder.path("/api/flights/search")
                                            .queryParam("departureTime", "2017-07-20T00:00Z")
                                            .build())
                     .exchange()
                     .expectStatus()
                     .isOk()
                     .expectBody()
                     .jsonPath("$.content").isEmpty();
    }

    @Test
    @DisplayName("Searching by arrival time correctly filters the results")
    void searchingByArrivalTimeCorrectlyFiltersTheResults() {
        webTestClient.get()
                     .uri(builder -> builder.path("/api/flights/search")
                                            .queryParam("arrivalTime", "2017-08-05T20:00+03")
                                            .build())
                     .exchange()
                     .expectStatus()
                     .isOk()
                     .expectBody()
                     .jsonPath("$.content.length()").isEqualTo(1)
                     .jsonPath("$.content[0].flightId").isEqualTo(2);
    }

    @Test
    @DisplayName("Search by airport code returns exact matches")
    void searchByAirportCodeReturnsExactMatches() {
        webTestClient.get()
                     .uri(builder -> builder.path("/api/flights/search")
                                            .queryParam("departureAirport", "BZK")
                                            .build())
                     .exchange()
                     .expectStatus()
                     .isOk()
                     .expectBody()
                     .jsonPath("$.content.length()").isEqualTo(1)
                     .jsonPath("$.content[0].flightId").isEqualTo(2);
    }

    @Test
    @DisplayName("Searching by airport code is case-insensitive")
    void searchingByAirportCodeIsCaseInsensitive() {
        webTestClient.get()
                     .uri(builder -> builder.path("/api/flights/search")
                                            .queryParam("departureAirport", "bzK")
                                            .build())
                     .exchange()
                     .expectBody()
                     .jsonPath("$.content.length()").isEqualTo(1)
                     .jsonPath("$.content[0].flightId").isEqualTo(2);
    }

    @Test
    @DisplayName("Search by arrival airport returns matches")
    void searchByArrivalAirportReturnsMatches() {
        webTestClient.get()
                     .uri(builder -> builder.path("/api/flights/search")
                                            .queryParam("arrivalAirport", "bzk")
                                            .build())
                     .exchange()
                     .expectBody()
                     .jsonPath("$.content.length()").isEqualTo(1)
                     .jsonPath("$.content[0].flightId").isEqualTo(1);
    }

    @Test
    @DisplayName("Search returns partial matches to the airport name")
    void searchReturnsPartialMatchesToTheAirportName() {
        webTestClient.get()
                     .uri(builder -> builder.path("/api/flights/search")
                                            .queryParam("departureAirport", "bryan")
                                            .build())
                     .exchange()
                     .expectBody()
                     .jsonPath("$.content.length()").isEqualTo(1)
                     .jsonPath("$.content[0].flightId").isEqualTo(2);
    }

    @Test
    @DisplayName("Search returns partial matches to the arrival airport name")
    void searchReturnsPartialMatchesToTheArrivalAirportName() {
        webTestClient.get()
                     .uri(builder -> builder.path("/api/flights/search")
                                            .queryParam("arrivalAirport", "GUT")
                                            .build())
                     .exchange()
                     .expectBody()
                     .jsonPath("$.content.length()").isEqualTo(1)
                     .jsonPath("$.content[0].flightId").isEqualTo(2);
    }

    @Test
    @Sql(scripts = {TestConstants.POPULATE_SCRIPT_PATH, "classpath:/scripts/additional-airports-flights.sql"})
    @DisplayName("Search returns partials matches to departure city name")
    void searchReturnsPartialsMatchesToDepartureCityName() {
        webTestClient.get()
                     .uri(builder -> builder.path("/api/flights/search")
                                            .queryParam("departureAirport", "cow")
                                            .build())
                     .exchange()
                     .expectBody()
                     .jsonPath("$.content.length()").isEqualTo(1)
                     .jsonPath("$.content[0].flightId").isEqualTo(3);
    }

    @Test
    @Sql(scripts = {TestConstants.POPULATE_SCRIPT_PATH, "classpath:/scripts/additional-airports-flights.sql"})
    @DisplayName("Search returns multiple partial matches to arrival city name")
    void searchReturnsMultiplePartialMatchesToArrivalCityName() {
        webTestClient.get()
                     .uri(builder -> builder.path("/api/flights/search")
                                            .queryParam("arrivalAirport", "peter")
                                            .build())
                     .exchange()
                     .expectBody()
                     .jsonPath("$.content.length()").isEqualTo(2)
                     .jsonPath("$.content[0].flightId").isEqualTo(3);
    }

}