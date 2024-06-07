package com.example.afs.flightdataapi.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Positive;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.Length;

@Entity
public class AircraftsData {
    @Id
    @Length(min = 3, max = 3)
    @Column(nullable = false)
    // IATA aircraft code
    String aircraftCode;

    @JdbcTypeCode(SqlTypes.JSON)
    AircraftModel model;

    // Maximum flying distance in km
    @Positive
    int range;

    public AircraftsData(String aircraftCode, AircraftModel model, int range) {
        this.aircraftCode = aircraftCode;
        this.model = model;
        this.range = range;
    }

    public AircraftsData() {
    }

    public String getAircraftCode() {
        return aircraftCode;
    }

    public void setAircraftCode(String aircraftCode) {
        this.aircraftCode = aircraftCode;
    }

    public AircraftModel getModel() {
        return model;
    }

    public void setModel(AircraftModel model) {
        this.model = model;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AircraftsData that = (AircraftsData) o;
        return aircraftCode.equals(that.aircraftCode);
    }

    @Override
    public int hashCode() {
        return aircraftCode.hashCode();
    }

    @Override
    public String toString() {
        return "AircraftsData{" +
                "aircraftCode='" + aircraftCode + '\'' +
                ", model='" + model + '\'' +
                ", range=" + range +
                '}';
    }
}
