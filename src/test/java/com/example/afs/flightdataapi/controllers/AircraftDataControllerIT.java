package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.model.entities.AircraftsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.delegate.DatabaseDelegate;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Sql(scripts = "classpath:scripts/test-aircrafts-data-populate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AircraftDataControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
                                                    .withInitScript("scripts/init-db.sql");

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @DisplayName("Connection to database container established")
    void connectionToDatabaseContainerEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @RepeatedTest(2)
    @DisplayName("Finds all aircraft data")
    void findsAllAircraftData() {
        AircraftsData[] data = restTemplate.getForObject("/api/aircraft", AircraftsData[].class);
        assertThat(data.length).isEqualTo(1);
    }

}