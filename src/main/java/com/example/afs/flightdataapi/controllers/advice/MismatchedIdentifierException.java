package com.example.afs.flightdataapi.controllers.advice;

public class MismatchedIdentifierException extends RuntimeException {

    public MismatchedIdentifierException(String pathId, String requestId) {
        super("Mismatch between identifier provided in path (%s) and in the request body (%s)".formatted(pathId, requestId));
    }

    public String getReason() {
        return "The resource identifiers provided in the URL path and in the request body must match";
    }
}
