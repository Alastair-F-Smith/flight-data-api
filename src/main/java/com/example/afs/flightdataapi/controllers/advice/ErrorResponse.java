package com.example.afs.flightdataapi.controllers.advice;

import org.springframework.http.HttpStatusCode;

public record ErrorResponse(String message, int statusCode, String cause) {

    public static ErrorResponse from(Exception e, HttpStatusCode statusCode) {
        return new ErrorResponse(e.getMessage(), statusCode.value(), e.getCause().getMessage());
    }


}
