package com.example.afs.flightdataapi.model.entities;

import com.example.afs.flightdataapi.model.dto.FlightQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.rsocket.RSocketProperties;
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
                .and(byPointOfDeparture(filter.departureAirport()))
                .and(byPointOfArrival(filter.arrivalAirport()))
                .toPredicate(root, query, builder);
    }

    /*
     * Search for exact matches to airport code or partial matches to city or airport
     * names. All matches are case-insensitive.
     */
    public static Specification<Flight> byPointOfDeparture(String query) {
        return byDepartureAirport(query)
                .or(byDepartureAirportName(query))
                .or(byDepartureCity(query));
    }

    /*
     * Search for exact matches to arrival airport code or partial matches to city or airport
     * names. All matches are case-insensitive.
     */
    public static Specification<Flight> byPointOfArrival(String query) {
        return byArrivalAirport(query)
                .or(byArrivalAirportName(query))
                .or(byArrivalCity(query));
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

    private static Specification<Flight> byDepartureAirportName(String name) {
        return byAirportName(Flight_.departureAirport, name);
    }

    private static Specification<Flight> byArrivalAirportName(String name) {
        return byAirportName(Flight_.arrivalAirport, name);
    }

    private static Specification<Flight> byAirportName(SingularAttribute<Flight, Airport> attribute, String name) {
        return (root, query, builder) -> {
            Predicate predicate = null;
            if (name != null) {
                Join<Flight, Airport> airportJoin = root.join(attribute);
                logger.debug("Searching for partial name match in {} to {}", attribute.getName(), name);
                predicate = jsonFieldContains(airportJoin, builder, "airportName", "en", name);
            }
            return predicate;
        };
    }

    private static Predicate jsonFieldContains(Path<?> path, CriteriaBuilder builder, String jsonAttribute, String jsonField, String query) {
        return builder.like(
                builder.lower(builder.function("jsonb_extract_path_text",
                                               String.class,
                                               path.get(jsonAttribute),
                                               builder.literal(jsonField))),
                containsIgnoreCase(query));
    }

    private static String containsIgnoreCase(String s) {
        return "%" + s.toLowerCase() + "%";
    }

    public static Specification<Flight> byDepartureCity(String city) {
        return byCityName(Flight_.departureAirport, city);
    }

    public static Specification<Flight> byArrivalCity(String city) {
        return byCityName(Flight_.arrivalAirport, city);
    }

    private static Specification<Flight> byCityName(SingularAttribute<Flight, Airport> attribute, String city) {
        return (root, query, builder) -> {
            Predicate predicate = null;
            if (city != null) {
                Join<Flight, Airport> airportJoin = root.join(attribute);
                logger.debug("Searching for partial city match in {} to {}", attribute.getName(), city);
                predicate = jsonFieldContains(airportJoin, builder, "city", "en", city);
            }
            return predicate;
        };
    }
}
