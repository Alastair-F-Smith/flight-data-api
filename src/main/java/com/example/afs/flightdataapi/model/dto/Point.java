package com.example.afs.flightdataapi.model.dto;

import org.postgresql.geometric.PGpoint;

public record Point(double lon, double lat) {

    public static Point from(PGpoint pgPoint) {
        return new Point(pgPoint.x, pgPoint.y);
    }

    public PGpoint toPgPoint() {
        return new PGpoint(lon, lat);
    }

}
