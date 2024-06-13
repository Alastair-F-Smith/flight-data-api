package com.example.afs.flightdataapi.controllers.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.stream.Collectors;

public record ErrorResponse(String message, int statusCode, String reason, String request) {

    public static ErrorResponse from(Exception e, HttpServletRequest request, HttpStatusCode statusCode) {
        return new ErrorResponse(e.getMessage(), statusCode.value(), "", request.getRequestURI());
    }

    public static ErrorResponse from(NestedRuntimeException e, HttpServletRequest request, HttpStatusCode statusCode) {
        String cause = getRootCause(e);
        if (cause.isEmpty()) cause = e.getClass().getName();
        return new ErrorResponse(e.getMessage(), statusCode.value(), cause, request.getRequestURI());
    }

    public static ErrorResponse from(MismatchedIdentifierException e, HttpServletRequest request, HttpStatusCode statusCode) {
        return new ErrorResponse(e.getMessage(), statusCode.value(), e.getReason(), request.getRequestURI());
    }

    public static ErrorResponse from(CustomValidationException e, HttpServletRequest request, HttpStatusCode statusCode) {
        String cause = e.getErrors()
                        .getAllErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining("; "));
        return new ErrorResponse(e.getMessage(), statusCode.value(), cause, request.getRequestURI());
    }

    public static ErrorResponse from(MethodArgumentNotValidException e, HttpServletRequest request, HttpStatusCode statusCode) {
        String cause = formatFieldErrorDefaultMessages(e);
        String invalidFields = formatInvalidFields(e);
        return new ErrorResponse("Error when validating input data for fields:" + invalidFields,
                                 statusCode.value(),
                                 cause,
                                 request.getRequestURI());
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
                .map(fieldError -> fieldError.getField() + ": rejected value '" +
                        fieldError.getRejectedValue() + "'")
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
