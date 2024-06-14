package com.example.afs.flightdataapi.model.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.postgresql.geometric.PGpoint;

import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

class AirportTests {

    Airport airport1;

    @BeforeEach
    void setUp() {
        airport1 = new Airport("BZK",
                               new TranslatedField("Bryansk Airport", "Брянск"),
                               new TranslatedField("Bryansk", "Брянск"),
                               new PGpoint(34.1763992309999978, 53.2141990661999955),
                               TimeZone.getTimeZone("Europe/Moscow"));
    }

    @Test
    @DisplayName("Can set airport name by providing English only when airport name is null")
    void canSetAirportNameByProvidingEnglishOnlyWhenAirportNameIsNull() {
        Airport airport = new Airport();
        airport.setAirportName("Heathrow");
        assertThat(airport.getAirportName().en()).isEqualTo("Heathrow");
        assertThat(airport.getAirportName().ru()).isEmpty();
    }

    @Test
    @DisplayName("Can update english airport name while retaining existing Russian translation")
    void canUpdateEnglishAirportNameWhileRetainingExistingRussianTranslation() {
        airport1.setAirportName("Gatwick");
        assertThat(airport1.getAirportName().en()).isEqualTo("Gatwick");
        assertThat(airport1.getAirportName().ru()).isEqualTo("Брянск");
    }

    @Test
    @DisplayName("Can update the russian airport name by specifying the language")
    void canUpdateTheRussianAirportNameBySpecifyingTheLanguage() {
        airport1.setAirportName("Test", SupportedLanguages.RUSSIAN);
        assertThat(airport1.getAirportName().en()).isEqualTo("Bryansk Airport");
        assertThat(airport1.getAirportName().ru()).isEqualTo("Test");
    }

    @Test
    @DisplayName("Can set city name by providing the english version only when the field is null")
    void canSetCityNameByProvidingTheEnglishVersionOnlyWhenTheFieldIsNull() {
        Airport airport = new Airport();
        airport.setCity("London");
        assertThat(airport.getCity().en()).isEqualTo("London");
        assertThat(airport.getCity().ru()).isEmpty();
    }

    @Test
    @DisplayName("Can update the english city name only")
    void canUpdateTheEnglishCityNameOnly() {
        airport1.setCity("London");
        assertThat(airport1.getCity().en()).isEqualTo("London");
        assertThat(airport1.getCity().ru()).isEqualTo("Брянск");
    }

    @Test
    @DisplayName("Can update the russian city name by specifying the language")
    void canUpdateTheRussianCityNameBySpecifyingTheLanguage() {
        airport1.setCity("Test", SupportedLanguages.RUSSIAN);
        assertThat(airport1.getCity().en()).isEqualTo("Bryansk");
        assertThat(airport1.getCity().ru()).isEqualTo("Test");
    }

}