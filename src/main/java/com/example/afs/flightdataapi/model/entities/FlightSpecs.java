package com.example.afs.flightdataapi.model.entities;

import com.example.afs.flightdataapi.model.dto.FlightQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZonedDateTime;

public class FlightSpecs implements Specification<Flight> {

    private final FlightQuery filter;
    private static final Logger logger = LoggerFactory.getLogger(FlightSpecs.class);

    public FlightSpecs(FlightQuery filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<Flight> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        return byDepartureTime(filter.departureTime())
                .and(byArrivalTime(filter.arrivalTime()))
                .and(byDepartureAirport(filter.departureAirport()))
                .and(byArrivalAirport(filter.arrivalAirport()))
                .toPredicate(root, query, builder);
    }

    public static Specification<Flight> byDepartureTime(ZonedDateTime departureTime) {
        return byTime(Flight_.scheduledDeparture, departureTime);
    }

    public static Specification<Flight> byArrivalTime(ZonedDateTime arrivalTime) {
        return byTime(Flight_.scheduledArrival, arrivalTime);
    }

    private static Specification<Flight> byTime(SingularAttribute<Flight, ZonedDateTime> attribute, ZonedDateTime time) {
        return (root, query, builder) -> {
            Predicate predicate = null;
            if (time != null) {
                logger.debug("Filtering by {} between {} and {}", attribute.getName(), time, time.plusDays(1));
                predicate = builder.between(root.get(attribute), time, time.plusDays(1));
            }
            return predicate;
        };
    }

    public static Specification<Flight> byDepartureAirport(String airport) {
        return byAirport(Flight_.departureAirport, airport);
    }

    public static Specification<Flight> byArrivalAirport(String airport) {
        return byAirport(Flight_.arrivalAirport, airport);
    }

    private static Specification<Flight> byAirport(SingularAttribute<Flight, Airport> attribute, String airport) {
        return (root, query, builder) -> {
            Predicate predicate = null;
            if (airport != null) {
                Join<Flight, Airport> airportJoin = root.join(attribute);
                logger.debug("Searching for {} with airport code matching {}", attribute.getName(), airport);
                predicate = builder.like(builder.lower(airportJoin.get(Airport_.airportCode)), airport.toLowerCase());
            }
            return predicate;
        };
    }
}
