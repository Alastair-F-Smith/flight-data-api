package com.example.afs.flightdataapi.controllers.advice;

import org.springframework.validation.Errors;

public class CustomValidationException extends RuntimeException {

    private Errors errors;

    public CustomValidationException(String id, Errors errors) {
        super("Error validating object with ID: " + id);
        this.errors = errors;
    }

    public Errors getErrors() {
        return errors;
    }
}
