package com.example.afs.flightdataapi.services.pricing;

import com.example.afs.flightdataapi.model.entities.FareConditions;
import com.example.afs.flightdataapi.model.repositories.TicketFlightsRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TicketPriceCalculator {

    private final TicketFlightsRepository ticketFlightsRepository;
    private final Map<Fare, BigDecimal> fares = new HashMap<>();
    private final Map<FareConditions, BigDecimal> averageFares = new HashMap<>();

    public TicketPriceCalculator(TicketFlightsRepository ticketFlightsRepository) {
        this.ticketFlightsRepository = ticketFlightsRepository;
    }

    /*
     * Get the fare price for the provided flight ID - fare conditions combination.
     * The price returned will be the minimum price for that combination in the dataset.
     * If the provided combination is not present in the data, a fallback price is
     * returned, calculated as the overall average price for the provided fare condition
     * rounded to the nearest 10.
     */
    public BigDecimal getPrice(Integer flightId, FareConditions fareConditions) {
        if (fares.isEmpty()) {
            initFares();
            initAverageFares();
        }
        Fare fare = new Fare(flightId, fareConditions);
        return fares.getOrDefault(fare, averageFares.get(fareConditions));
    }


    private void initFares() {
        List<FareAmounts> fareAmounts = ticketFlightsRepository.findFareAmounts();
        for (var fareAmount : fareAmounts) {
            Fare fare = new Fare(fareAmount.flightId(), fareAmount.fareConditions());
            fares.put(fare, fareAmount.amount());
        }
    }

    /*
     * Calculate fallback pricing for each fare condition category. This is defined as the average for that
     * fare condition across all tickets, rounded to the nearest 10.
     */
    private void initAverageFares() {
        List<FareAmounts> fareAmounts = ticketFlightsRepository.findAverageFareAmountByFareConditions();
        for (var fareAmount : fareAmounts) {
            averageFares.put(fareAmount.fareConditions(), fareAmount.amount().setScale(-1, RoundingMode.HALF_UP));
        }
    }
}
