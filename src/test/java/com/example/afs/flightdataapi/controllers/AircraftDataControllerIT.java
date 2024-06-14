package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.controllers.advice.DataAccessAdvice;
import com.example.afs.flightdataapi.controllers.advice.ErrorResponse;
import com.example.afs.flightdataapi.model.entities.TranslatedField;
import com.example.afs.flightdataapi.model.entities.AircraftsData;
import com.example.afs.flightdataapi.testutils.TestConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Sql(scripts = TestConstants.POPULATE_SCRIPT_PATH, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AircraftDataControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(TestConstants.POSTGRES_DOCKER_IMAGE)
                                                    .withInitScript(TestConstants.INIT_SCRIPT_PATH);

    WebTestClient webTestClient;

    @Autowired
    AircraftDataController aircraftDataController;

    @Autowired
    DataAccessAdvice dataAccessAdvice;

    @Autowired
    ObjectMapper objectMapper;

    AircraftsData aircraft1;
    AircraftsData aircraft2;

    @BeforeEach
    void setup() {
        webTestClient = MockMvcWebTestClient.bindToController(aircraftDataController)
                                            .controllerAdvice(dataAccessAdvice)
                                            .build();
        aircraft1 = new AircraftsData("ABC", new TranslatedField("Boeing", "Boeing"), 10000);
        aircraft2 = new AircraftsData("123", new TranslatedField("Airbus", "Airbus"), 15000);
    }

    @Test
    @DisplayName("Connection to database container established")
    void connectionToDatabaseContainerEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @WithMockUser
    @RepeatedTest(2)
    @DisplayName("Finds all aircraft data")
    void findsAllAircraftData() {
        assertThatNumberOfRecordsEquals(1);
    }

    private void assertThatNumberOfRecordsEquals(int number) {
        webTestClient
                .get()
                .uri("/api/aircraft")
                .exchange()
                .expectBodyList(AircraftsData.class)
                .hasSize(number);
    }

    @WithMockUser
    @Test
    @DisplayName("Get by ID returns a response containing the correct data")
    void getByIdReturnsAResponseContainingTheCorrectData() {
        String code = "773";
        AircraftsData expected = new AircraftsData(code, new TranslatedField("Boeing", "Boeing"), 11100);
        webTestClient
                .get()
                .uri("/api/aircraft/" + code)
                .exchange()
                .expectBody(AircraftsData.class)
                .isEqualTo(expected);
    }

    @WithMockUser
    @Test
    @DisplayName("Add aircraft saves the new aircraft data in the database")
    void addAircraftSavesTheNewAircraftDataInTheDatabase() {
        webTestClient
                .post()
                .uri("/api/aircraft")
                .bodyValue(aircraft1)
                .exchange();

        assertThatNumberOfRecordsEquals(2);
    }

    @WithMockUser
    @Test
    @DisplayName("Add aircraft returns a response body containing the data that was saved")
    void addAircraftReturnsAResponseBodyContainingTheDataThatWasSaved() {
        webTestClient
                .post()
                .uri("/api/aircraft")
                .bodyValue(aircraft1)
                .exchange()
                .expectBody(AircraftsData.class)
                .isEqualTo(aircraft1);
    }

    @WithMockUser
    @Test
    @DisplayName("Add aircraft throws a validation exception when attempting to save invalid data")
    void addAircraftThrowsAValidationExceptionWhenAttemptingToSaveInvalidData() {
        AircraftsData invalidData = new AircraftsData("AA", aircraft1.getModel(), aircraft1.getRange());
        webTestClient
                .post()
                .uri("/api/aircraft")
                .bodyValue(invalidData)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(ErrorResponse.class)
                ;
    }

    @WithMockUser
    @Test
    @DisplayName("Delete aircraft deletes the aircraft data when it exists")
    void deleteAircraftDeletesTheAircraftDataWhenItExists() {
        String code = "773";
        webTestClient
                .delete()
                .uri("/api/aircraft/" + code)
                .exchange()
                .expectStatus()
                .isOk();

        assertThatNumberOfRecordsEquals(0);
    }

    @WithMockUser
    @Test
    @DisplayName("Update aircraft data correctly updates the data")
    void updateAircraftDataCorrectlyUpdatesTheData() {
        String code = "773";
        AircraftsData update = new AircraftsData(code, new TranslatedField("Airbus", "Airbus"), 13000);
        webTestClient
                .put()
                .uri("/api/aircraft/" + code)
                .bodyValue(update)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient
                .get()
                .uri("/api/aircraft/" + code)
                .exchange()
                .expectBody(AircraftsData.class)
                .isEqualTo(update);
    }

}