package com.example.afs.flightdataapi.model.repositories;

import com.example.afs.flightdataapi.model.entities.FareConditions;
import com.example.afs.flightdataapi.services.pricing.FareAmounts;
import com.example.afs.flightdataapi.testutils.TestConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@DataJpaTest
@TestPropertySource(properties = {TestConstants.PROPERTIES_DB_REPLACE_NONE})
class TicketFlightsRepositoryTests {

    @Autowired
    TicketFlightsRepository ticketFlightsRepository;

    @Test
    @DisplayName("Get fare amounts returns a non-empty list of fare amounts")
    void getFareAmountsReturnsANonEmptyListOfFareAmounts() {
        List<FareAmounts> fareAmounts = ticketFlightsRepository.findFareAmounts();
        assertThat(fareAmounts).isNotEmpty();
        System.out.println(fareAmounts.getFirst());
    }

    @Test
    @DisplayName("Find average fare amount returns a entries for each fare condition")
    void findAverageFareAmountReturnsAEntriesForEachFareCondition() {
        List<FareAmounts> fareAmounts = ticketFlightsRepository.findAverageFareAmountByFareConditions();
        assertThat(fareAmounts.size()).isEqualTo(FareConditions.values().length);
    }

}