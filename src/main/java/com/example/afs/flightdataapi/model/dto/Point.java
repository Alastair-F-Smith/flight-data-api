package com.example.afs.flightdataapi.model.dto;

import org.hibernate.validator.constraints.Range;
import org.postgresql.geometric.PGpoint;

public record Point(
        @Range(min = -180L, max = 180L, message = LONGITUDE_RANGE_MESSAGE)
        double lon,

        @Range(min = -90L, max = 90L, message = LATITUDE_RANGE_MESSAGE)
        double lat) {

    public static final String LONGITUDE_RANGE_MESSAGE = "Longitude must be between -180 and 180 degrees";
    public static final String LATITUDE_RANGE_MESSAGE = "Latitude must be between -90 and 90 degrees";

    public static Point from(PGpoint pgPoint) {
        return new Point(pgPoint.x, pgPoint.y);
    }

    public PGpoint toPgPoint() {
        return new PGpoint(lon, lat);
    }

}
