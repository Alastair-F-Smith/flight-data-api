package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.model.entities.AircraftsData;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

public class AircraftModelAssembler implements SimpleRepresentationModelAssembler<AircraftsData> {
    @Override
    public void addLinks(EntityModel<AircraftsData> resource) {
        resource.add(linkTo(methodOn(AircraftDataController.class).getAllAircraft()).withRel("allAircraft"));
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<AircraftsData>> resources) {

    }
}
