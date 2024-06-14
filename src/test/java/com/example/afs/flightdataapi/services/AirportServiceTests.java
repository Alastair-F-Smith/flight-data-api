package com.example.afs.flightdataapi.services;

import com.example.afs.flightdataapi.model.dto.AirportDto;
import com.example.afs.flightdataapi.model.dto.Point;
import com.example.afs.flightdataapi.model.entities.Airport;
import com.example.afs.flightdataapi.model.entities.TranslatedField;
import com.example.afs.flightdataapi.model.repositories.AirportRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.postgresql.geometric.PGpoint;

import java.util.Optional;
import java.util.TimeZone;

import static org.mockito.Mockito.*;

class AirportServiceTests {

    AirportRepository airportRepository;
    AirportService airportService;

    Airport airport1;

    @BeforeEach
    void setUp() {
        airportRepository = mock();
        airportService = new AirportService(airportRepository);

        airport1 = new Airport("BZK",
                               new TranslatedField("Bryansk Airport", "Брянск"),
                               new TranslatedField("Bryansk", "Брянск"),
                               new PGpoint(34.1763992309999978, 53.2141990661999955),
                               TimeZone.getTimeZone("Europe/Moscow"));
    }

    @Test
    @DisplayName("Airport data is updated from a DTO when an existing record is found")
    void airportDataIsUpdatedFromADtoWhenAnExistingRecordIsFound() {
        when(airportRepository.findById("BZK")).thenReturn(Optional.of(airport1));

        AirportDto airportDto = new AirportDto("BZK", "Test Airport", "Test", new Point(0.0, 0.0), TimeZone.getDefault());

        Airport updated = airportService.fromDto(airportDto);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(updated.getAirportCode()).isEqualTo("BZK");
            softly.assertThat(updated.getAirportName().en()).isEqualTo("Test Airport");
            softly.assertThat(updated.getAirportName().ru()).isEqualTo("Брянск");
            softly.assertThat(updated.getCity().en()).isEqualTo("Test");
            softly.assertThat(updated.getCity().ru()).isEqualTo("Брянск");
            softly.assertThat(updated.getCoordinates()).isEqualTo(new PGpoint(0.0, 0.0));
            softly.assertThat(updated.getTimezone()).isEqualTo(TimeZone.getDefault());
        });
    }

    @Test
    @DisplayName("Airport data is created from a DTO when no existing record is found")
    void airportDataIsCreatedFromADtoWhenNoExistingRecordIsFound() {
        when(airportRepository.findById("BZK")).thenReturn(Optional.empty());

        AirportDto airportDto = new AirportDto("BZK", "Test Airport", "Test", new Point(0.0, 0.0), TimeZone.getDefault());

        Airport updated = airportService.fromDto(airportDto);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(updated.getAirportCode()).isEqualTo("BZK");
            softly.assertThat(updated.getAirportName().en()).isEqualTo("Test Airport");
            softly.assertThat(updated.getAirportName().ru()).isEqualTo("");
            softly.assertThat(updated.getCity().en()).isEqualTo("Test");
            softly.assertThat(updated.getCity().ru()).isEqualTo("");
            softly.assertThat(updated.getCoordinates()).isEqualTo(new PGpoint(0.0, 0.0));
            softly.assertThat(updated.getTimezone()).isEqualTo(TimeZone.getDefault());
        });
    }

}