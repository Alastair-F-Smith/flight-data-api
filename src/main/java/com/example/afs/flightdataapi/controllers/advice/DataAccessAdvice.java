package com.example.afs.flightdataapi.controllers.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.NestedRuntimeException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DataAccessAdvice {

    @ExceptionHandler({DataAccessException.class, TransactionException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleDataAccessException(NestedRuntimeException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.from(e, request, HttpStatus.BAD_REQUEST);
        return ResponseEntity.badRequest()
                .body(response);
    }

    @ExceptionHandler({DataNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleDataNotFoundException(DataNotFoundException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.from(e, request, HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.from(e, request, HttpStatus.BAD_REQUEST);
        return ResponseEntity.badRequest()
                             .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.from(e, request, HttpStatus.BAD_REQUEST);
        return ResponseEntity.badRequest()
                             .body(response);
    }

    @ExceptionHandler(CustomValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleCustomValidationException(CustomValidationException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.from(e, request, HttpStatus.BAD_REQUEST);
        return ResponseEntity.badRequest()
                             .body(response);
    }

    @ExceptionHandler(MismatchedIdentifierException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMismatchedIdentifierException(MismatchedIdentifierException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.from(e, request, HttpStatus.BAD_REQUEST);
        return ResponseEntity.badRequest()
                             .body(response);
    }

    @ExceptionHandler(FlightAlreadyAddedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleFlightAlreadyAddedException(FlightAlreadyAddedException e, HttpServletRequest request) {
        var response = ErrorResponse.from(e, request, HttpStatus.BAD_REQUEST);
        return ResponseEntity.badRequest()
                             .body(response);
    }
}
