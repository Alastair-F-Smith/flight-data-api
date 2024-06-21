package com.example.afs.flightdataapi.services.pricing;

import com.example.afs.flightdataapi.model.entities.FareConditions;
import com.example.afs.flightdataapi.testutils.TestConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withinPercentage;

@DataJpaTest
@Import(TicketPriceCalculator.class)
@TestPropertySource(properties = {TestConstants.PROPERTIES_DB_REPLACE_NONE})
class TicketPriceCalculatorTests {

    @Autowired
    TicketPriceCalculator ticketPriceCalculator;

    @ParameterizedTest
    @MethodSource("getFareAmounts")
    @DisplayName("Get price calculates the correct price for a given fare")
    void getPriceCalculatesTheCorrectPriceForAGivenFare(int flightId, FareConditions fareConditions, int expectedAmount) {
        BigDecimal amount = ticketPriceCalculator.getPrice(flightId, fareConditions);
        assertThat(amount).isCloseTo(BigDecimal.valueOf(expectedAmount), withinPercentage(1));
    }

    static Stream<Arguments> getFareAmounts() {
        return Stream.of(
                Arguments.of(1, FareConditions.BUSINESS, 20000),
                Arguments.of(46, FareConditions.ECONOMY, 6700),
                Arguments.of(254, FareConditions.COMFORT, 47400)
        );
    }

    @ParameterizedTest
    @MethodSource("getMissingFareAmounts")
    @DisplayName("Get price returns a fallback amount if the fare details are not in the database")
    void getPriceReturnsAFallbackAmountIfTheFareDetailsAreNotInTheDatabase(FareConditions fareConditions, int expectedAmount) {
        BigDecimal amount = ticketPriceCalculator.getPrice(230, fareConditions);
        System.out.println(amount.intValue());
        assertThat(amount).isCloseTo(BigDecimal.valueOf(expectedAmount), withinPercentage(1));
    }

    static Stream<Arguments> getMissingFareAmounts() {
        return Stream.of(
                Arguments.of(FareConditions.ECONOMY, 15960),
                Arguments.of(FareConditions.COMFORT, 32740),
                Arguments.of(FareConditions.BUSINESS, 51140)
        );
    }
}