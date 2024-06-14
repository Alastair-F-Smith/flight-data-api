package com.example.afs.flightdataapi.model.repositories;

import com.example.afs.flightdataapi.model.entities.TranslatedField;
import com.example.afs.flightdataapi.model.entities.AircraftsData;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.test.database.replace=none",
        "spring.datasource.url=jdbc:tc:postgresql:16-alpine:///db?TC_INITSCRIPT=scripts/init-db.sql"
})
@Sql("classpath:scripts/test-aircrafts-data-populate.sql")
class AircraftsDataRepositoryTests {

    @Autowired
    AircraftsDataRepository repository;

    @Autowired
    SeatRepository seatRepository;
    @Autowired
    private AircraftsDataRepository aircraftsDataRepository;

    @Test
    @DisplayName("Repository reads data data from the database")
    void repositoryReadsDataDataFromTheDatabase() {
        assertThat(repository.findAll()).isNotEmpty();
    }

    @Test
    @DisplayName("Repository returns data for the correct aircraft when searching by code")
    void repositoryReturnsDataForTheCorrectAircraftWhenSearchingByCode() {
        AircraftsData data = repository.findById("773").orElse(null);
        System.out.println(data);
        assertThat(data).isNotNull();
    }

    @Test
    @DisplayName("Aircraft data can be saved to the database")
    void aircraftDataCanBeSavedToTheDatabase() {
        AircraftsData data = new AircraftsData("ABC", new TranslatedField("Airbus", "Airbus"), 10_000);
        AircraftsData saved = repository.save(data);
        assertThat(saved.getAircraftCode()).isEqualTo("ABC");
    }

    @Test
    @DisplayName("The number of records increases after saving aircraft data")
    void theNumberOfRecordsIncreasesAfterSavingAircraftData() {
        long initialCount = repository.count();
        AircraftsData data = new AircraftsData("XXX", new TranslatedField("Airbus", "Airbus"), 10_000);
        repository.save(data);
        assertThat(initialCount).isLessThan(repository.count());
    }

    @Test
    @DisplayName("The repository throws an exception when trying to save invalid data")
    void theRepositoryThrowsAnExceptionWhenTryingToSaveInvalidData() {
        AircraftsData data = new AircraftsData("AB", new TranslatedField("Airbus", "Airbus"), 10_000);
        assertThatExceptionOfType(ConstraintViolationException.class).isThrownBy(() -> {
            repository.save(data);
            repository.flush();
        }).withMessageContaining("length must be between 3 and 3");
    }
}