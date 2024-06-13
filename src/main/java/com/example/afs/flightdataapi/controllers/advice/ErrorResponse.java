package com.example.afs.flightdataapi.controllers.advice;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.stream.Collectors;

public record ErrorResponse(String message, int statusCode, String cause) {

    public static ErrorResponse from(Exception e, HttpStatusCode statusCode) {
        String cause = getRootCause(e);
        if (cause.isEmpty()) cause = e.getClass().getName();
        return new ErrorResponse(e.getMessage(), statusCode.value(), cause);
    }

    public static ErrorResponse from(CustomValidationException e, HttpStatusCode statusCode) {
        String cause = e.getErrors()
                        .getAllErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining("; "));
        return new ErrorResponse(e.getMessage(), statusCode.value(), cause);
    }

    public static ErrorResponse from(MethodArgumentNotValidException e, HttpStatusCode statusCode) {
        String cause = formatFieldErrorDefaultMessages(e);
        String invalidFields = formatInvalidFields(e);
        return new ErrorResponse("Error when validating input data for fields:" + invalidFields, statusCode.value(), cause);
    }

    private static String formatFieldErrorDefaultMessages(MethodArgumentNotValidException e) {
        return e.getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
    }

    private static String formatInvalidFields(MethodArgumentNotValidException e) {
        return e.getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": rejected value " +
                        fieldError.getRejectedValue())
                .collect(Collectors.joining(", ", " [", "]"));
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
