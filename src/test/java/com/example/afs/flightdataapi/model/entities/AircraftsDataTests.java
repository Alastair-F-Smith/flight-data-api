package com.example.afs.flightdataapi.model.entities;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class AircraftsDataTests {


    static Validator validator;

    @BeforeAll
    public static void setUpAll() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Nested
    @DisplayName("Aircraft code validation")
    class AircraftCodeValidation {

        @ParameterizedTest
        @ValueSource(strings = {"AB", "ABCD", ""})
        @DisplayName("Creating aircraft data with a code not exactly 3 characters long is an error")
        void creatingAircraftDataWithACodeNotExactly3CharactersLongIsAnError(String code) {
            AircraftsData data = new AircraftsData(code, new TranslatedField("test", "test"), 100);
            var constraintViolations =  validator.validate(data);
            assertThat(constraintViolations).hasSize(1);
        }

        @Test
        @DisplayName("Creating aircraft data with a code that is 3 characters long does not violate any constraints")
        void creatingAircraftDataWithACodeThatIs3CharactersLongDoesNotViolateAnyConstraints() {
            AircraftsData data = new AircraftsData("123", new TranslatedField("test", "test"), 100);
            var constraintViolations =  validator.validate(data);
            assertThat(constraintViolations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Range validation")
    class RangeValidation {

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -10_000, Integer.MIN_VALUE})
        @DisplayName("Ranges that are less than or equal to 0 result in constraint violations")
        void rangesThatAreLessThanOrEqualTo0ResultInConstraintViolations(int range) {
            AircraftsData data = new AircraftsData("123", new TranslatedField("test", "test"), range);
            var constraintViolations =  validator.validate(data);
            assertThat(constraintViolations).hasSize(1);
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 100, 9999, Integer.MAX_VALUE})
        @DisplayName("Positive ranges do not violate constraints")
        void positiveRangesDoNotViolateConstraints(int range) {
            AircraftsData data = new AircraftsData("123", new TranslatedField("test", "test"), range);
            var constraintViolations =  validator.validate(data);
            assertThat(constraintViolations).isEmpty();
        }

    }


}