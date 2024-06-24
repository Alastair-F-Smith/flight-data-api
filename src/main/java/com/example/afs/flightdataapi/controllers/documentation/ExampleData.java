package com.example.afs.flightdataapi.controllers.documentation;

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
}
