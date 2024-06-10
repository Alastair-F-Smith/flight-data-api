package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.model.entities.AircraftModel;
import com.example.afs.flightdataapi.model.entities.AircraftsData;
import com.example.afs.flightdataapi.model.repositories.AircraftsDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.is;

@WebMvcTest(AircraftDataController.class)
class AircraftDataControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AircraftsDataRepository aircraftsDataRepository;

    AircraftsData aircraft1;
    AircraftsData aircraft2;

    @BeforeEach
    void setUp() {
        aircraft1 = new AircraftsData("ABC", new AircraftModel("Boeing", "Boeing"), 10000);
        aircraft2 = new AircraftsData("123", new AircraftModel("Airbus", "Airbus"), 15000);
    }

    @Test
    @DisplayName("Find all aircraft data returns a 200 response status")
    void findAllAircraftDataReturnsA200ResponseStatus() throws Exception {
        mockMvc.perform(get("/api/aircraft"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Find all aircraft data returns correctly formatted data")
    void findAllAircraftDataReturnsCorrectlyFormattedData() throws Exception {
        when(aircraftsDataRepository.findAll())
                .thenReturn(List.of(aircraft1, aircraft2));

        mockMvc.perform(get("/api/aircraft"))
                .andExpectAll(
                        jsonPath("$[0].aircraftCode", is(aircraft1.getAircraftCode())),
                        jsonPath("$[0].model.en", is(aircraft1.getModel().en())),
                        jsonPath("$[0].range", is(aircraft1.getRange())),
                        jsonPath("$[1].aircraftCode", is(aircraft2.getAircraftCode())),
                        jsonPath("$[1].model.en", is(aircraft2.getModel().en())),
                        jsonPath("$[1].range", is(aircraft2.getRange()))
                );
    }

}