package com.example.afs.flightdataapi.model.entities;

import com.example.afs.flightdataapi.services.converters.PGPointType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.Length;
import org.postgresql.geometric.PGpoint;
import java.util.TimeZone;

@Entity
@Table(name = "airports_data")
public class Airport {

    public static final String AIRPORT_CODE_LENGTH_MESSAGE = "Airport code must be exactly 3 characters long";
    public static final String AIRPORT_CODE_NULL_MESSAGE = "An airport code must be provided";

    @NotBlank(message = AIRPORT_CODE_NULL_MESSAGE)
    @Length(min = 3, max = 3, message = AIRPORT_CODE_LENGTH_MESSAGE)
    @Id
    String airportCode;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    TranslatedField airportName;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    TranslatedField city;

    @NotNull
    @Type(PGPointType.class)
    PGpoint coordinates;

    @NotNull
    TimeZone timezone;

    public Airport(String airportCode, TranslatedField airportName, TranslatedField city, PGpoint coordinates, TimeZone timezone) {
        this.airportCode = airportCode;
        this.airportName = airportName;
        this.city = city;
        this.coordinates = coordinates;
        this.timezone = timezone;
    }

    public Airport() {
        this.airportName = new TranslatedField("", "");
        this.city = new TranslatedField("", "");
        this.coordinates = new PGpoint();
        this.timezone = TimeZone.getDefault();
    }

    public @NotBlank(message = AIRPORT_CODE_NULL_MESSAGE) @Length(min = 3, max = 3, message = AIRPORT_CODE_LENGTH_MESSAGE) String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(@NotBlank(message = AIRPORT_CODE_NULL_MESSAGE) @Length(min = 3, max = 3, message = AIRPORT_CODE_LENGTH_MESSAGE) String airportCode) {
        this.airportCode = airportCode;
    }

    public @NotNull TranslatedField getAirportName() {
        return airportName;
    }

    public void setAirportName(@NotNull TranslatedField airportName) {
        this.airportName = airportName;
    }

    public void setAirportName(@NotBlank String airportName, SupportedLanguages language) {
        if (this.airportName == null) {
            this.airportName = TranslatedField.fromPartial(airportName, language);
        } else {
            this.airportName = this.airportName.with(airportName, language);
        }
    }

    public void setAirportName(@NotBlank String airportName) {
        setAirportName(airportName, SupportedLanguages.ENGLISH);
    }

    public @NotNull TranslatedField getCity() {
        return city;
    }

    public void setCity(@NotNull TranslatedField city) {
        this.city = city;
    }

    public void setCity(@NotBlank String city, SupportedLanguages language) {
        if (this.city == null) {
            this.city = TranslatedField.fromPartial(city, language);
        } else {
            this.city = this.city.with(city, language);
        }
    }

    public void setCity(@NotBlank String city) {
        setCity(city, SupportedLanguages.ENGLISH);
    }

    public @NotNull PGpoint getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(@NotNull PGpoint coordinates) {
        this.coordinates = coordinates;
    }

    public @NotNull TimeZone getTimezone() {
        return timezone;
    }

    public void setTimezone(@NotNull TimeZone timezone) {
        this.timezone = timezone;
    }

    @Override
    public String toString() {
        return "Airport{" +
                "airportCode='" + airportCode + '\'' +
                ", airportName=" + airportName +
                ", city=" + city +
                ", coordinates='" + coordinates + '\'' +
                ", timezone=" + timezone +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Airport airport = (Airport) o;
        return airportCode.equals(airport.airportCode);
    }

    @Override
    public int hashCode() {
        return airportCode.hashCode();
    }
}
