package com.example.afs.flightdataapi.controllers.advice;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DataAccessAdvice {

    @ExceptionHandler({DataAccessException.class, TransactionException.class})
    public ResponseEntity<ErrorResponse> handleDataAccessException(NestedRuntimeException e) {
        ErrorResponse response = ErrorResponse.from(e, HttpStatus.BAD_REQUEST);
        return ResponseEntity.badRequest()
                .body(response);
    }
}
