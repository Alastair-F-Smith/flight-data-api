package com.example.afs.flightdataapi.controllers.documentation;

import io.swagger.v3.oas.models.examples.Example;

public class ExampleData {
    public static final String AIRCRAFT_VALID = """
        {
          "aircraftCode": "773",
          "model": {
            "en": "Boeing 777-300",
            "ru": "Боинг 777-300"
          },
          "range": 11100
        }
        """;
    public static final String AIRCRAFT_INVALID = """
        {
          "aircraftCode": "7730",
          "model": {
            "en": "Boeing 777-300",
            "ru": "Боинг 777-300"
          },
          "range": 0
        }
        """;

    public static final String SEAT_VALID = """
            {
              "aircraftCode": "773",
              "seatNo": "19A",
              "fareConditions": "ECONOMY"
            }
            """;

    public static final String SEAT_INVALID = """
            {
              "aircraftCode": "7730",
              "seatNo": "199",
              "fareConditions": "economy"
            }
            """;

    public static final String AIRPORT_VALID = """
            {
              "airportCode": "VAL",
              "name": "Valid Airport",
              "city": "St. Petersburg",
              "coordinates": {
                "lon": 30.2625007629394531,
                "lat": 59.8003005981445312
              },
              "timeZone": "Europe/London"
            }
            """;

    public static final String AIRPORT_INVALID = """
            {
              "airportCode": "IVLD",
              "name": " ",
              "city": "St. Petersburg",
              "coordinates": {
                "lon": 180.2625007629394531,
                "lat": -95.0
              },
              "timeZone": "Europe/London"
            }
            """;

    public static final String PERSONAL_DETAILS_VALID = """
            {
              "name": "John Doe",
              "email": "john.doe@email.com",
              "phone": "+448080400500"
            }
            """;

    public static final String PERSONAL_DETAILS_INVALID = """
            {
              "name": " ",
              "email": "john.doe.com",
              "phone": "++448080400500"
            }
            """;

    public static Example getExample(String path, ExampleTypes type) {
        String value = getValue(path, type);
        return new Example().description(type.getDescription()).value(value);
    }

    private static String getValue(String path, ExampleTypes type) {
        RequestTypes requestType = getRequestType(path);
        return type == ExampleTypes.VALID ? requestType.getValid() : requestType.getInvalid();
    }

    private static RequestTypes getRequestType(String path) {
        return switch(path) {
            case String s when s.contains("seat") -> RequestTypes.SEAT;
            case String s when s.contains("aircraft") -> RequestTypes.AIRCRAFT;
            case String s when s.contains("airport") -> RequestTypes.AIRPORT;
            case String s when s.contains("bookings") -> RequestTypes.PERSONAL_DETAILS;
            case null, default -> throw new IllegalArgumentException("Not a valid request type");
        };
    }
}
