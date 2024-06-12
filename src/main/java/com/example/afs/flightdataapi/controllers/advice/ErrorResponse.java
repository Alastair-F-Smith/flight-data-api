package com.example.afs.flightdataapi.controllers.advice;

import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatusCode;

public record ErrorResponse(String message, int statusCode, String cause) {

    public static ErrorResponse from(Exception e, HttpStatusCode statusCode) {
        String cause = getRootCause(e);
        return new ErrorResponse(e.getMessage(), statusCode.value(), cause);
    }

    private static String getRootCause(Exception e) {
        String cause = "";
        if (e instanceof NestedRuntimeException nested) {
            Throwable rootCause = nested.getRootCause();
            cause = maybeGetMessage(rootCause);
        }
        return cause;
    }

    private static String maybeGetMessage(Throwable e) {
        String message = e == null ? "" : e.getMessage();
        return message == null ? "" : message;
    }


}
