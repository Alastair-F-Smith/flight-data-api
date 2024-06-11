package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.config.SecurityConfig;
import com.example.afs.flightdataapi.controllers.advice.DataAccessAdvice;
import com.example.afs.flightdataapi.model.entities.AircraftModel;
import com.example.afs.flightdataapi.model.entities.AircraftsData;
import com.example.afs.flightdataapi.model.repositories.AircraftsDataRepository;
import com.example.afs.flightdataapi.services.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest({AircraftDataController.class, AuthController.class, DataAccessAdvice.class})
@Import({TokenService.class, SecurityConfig.class})
class AircraftDataControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AircraftsDataRepository aircraftsDataRepository;

    AircraftsData aircraft1;
    AircraftsData aircraft2;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        aircraft1 = new AircraftsData("ABC", new AircraftModel("Boeing", "Boeing"), 10000);
        aircraft2 = new AircraftsData("123", new AircraftModel("Airbus", "Airbus"), 15000);
    }

    @Test
    @DisplayName("Find all aircraft data returns a 401 response status when user is not authenticated")
    void findAllAircraftDataReturnsA401ResponseStatusWhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/aircraft"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser
    @Test
    @DisplayName("Find all aircraft data returns a 200 response status")
    void findAllAircraftDataReturnsA200ResponseStatus() throws Exception {
        mockMvc.perform(get("/api/aircraft"))
                .andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    @DisplayName("Get aircraft data by id returns a response containing the correct data")
    void getAircraftDataByIdReturnsAResponseContainingTheCorrectData() throws Exception {
        when(aircraftsDataRepository.findById(anyString()))
                .thenReturn(Optional.of(aircraft1));

        mockMvc.perform(get("/api/aircraft/" + aircraft1.getAircraftCode()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.aircraftCode", is(aircraft1.getAircraftCode())),
                        jsonPath("$.model.en", is(aircraft1.getModel().en())),
                        jsonPath("$.range", is(aircraft1.getRange()))
                );
    }

    @WithMockUser
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

    @WithMockUser
    @Test
    @DisplayName("Add aircraft passes correct aircraft data to the repository")
    void addAircraftPassesCorrectAircraftDataToTheRepository() throws Exception {
        String json = objectMapper.writeValueAsString(aircraft1);

        when(aircraftsDataRepository.save(any(AircraftsData.class)))
                .thenReturn(aircraft1);

        mockMvc.perform(post("/api/aircraft")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(json)
                );

        verify(aircraftsDataRepository, times(1)).save(aircraft1);
    }

    @WithMockUser
    @Test
    @DisplayName("Add aircraft returns the correct location URL for the newly created resource")
    void addAircraftReturnsTheCorrectLocationUrlForTheNewlyCreatedResource() throws Exception {
        String json = objectMapper.writeValueAsString(aircraft1);

        when(aircraftsDataRepository.save(any(AircraftsData.class)))
                .thenReturn(aircraft1);

        String expectedLocation = "http://localhost/api/aircraft/" + aircraft1.getAircraftCode();

        mockMvc.perform(post("/api/aircraft")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string("location", is(expectedLocation)));
    }

    @WithMockUser
    @Test
    @DisplayName("Update aircraft returns a response containing the updated data")
    void updateAircraftReturnsAResponseContainingTheUpdatedData() throws Exception {
        when(aircraftsDataRepository.findById(anyString()))
                .thenReturn(Optional.of(aircraft1));

        when(aircraftsDataRepository.save(any(AircraftsData.class)))
                .thenAnswer(i -> i.getArgument(0, AircraftsData.class));

        AircraftsData updated = new AircraftsData(aircraft1.getAircraftCode(), new AircraftModel("Airbus", "Airbus"), 123456);

        mockMvc.perform(put("/api/aircraft/" + aircraft1.getAircraftCode())
                                .content(objectMapper.writeValueAsString(updated))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.aircraftCode", is(updated.getAircraftCode())),
                        jsonPath("$.model.en", is(updated.getModel().en())),
                        jsonPath("$.range", is(updated.getRange()))
                );
    }

    @WithMockUser
    @Test
    @DisplayName("Update aircraft returns a not found status code if the ID is not found")
    void updateAircraftReturnsANotFoundStatusCodeIfTheIdIsNotFound() throws Exception {
        when(aircraftsDataRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        when(aircraftsDataRepository.save(any(AircraftsData.class)))
                .thenAnswer(i -> i.getArgument(0, AircraftsData.class));

        AircraftsData updated = new AircraftsData("ZZZ", new AircraftModel("Airbus", "Airbus"), 123456);

        mockMvc.perform(put("/api/aircraft/" + aircraft1.getAircraftCode())
                                .content(objectMapper.writeValueAsString(updated))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    @DisplayName("Delete aircraft returns 200 status when aircraft record is found")
    void deleteAircraftReturns200StatusWhenAircraftRecordIsFound() throws Exception {
        when(aircraftsDataRepository.findById(anyString()))
                .thenReturn(Optional.of(aircraft1));

        mockMvc.perform(delete("/api/aircraft/" + aircraft1.getAircraftCode()))
                .andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    @DisplayName("Delete aircraft returns a response containing data for the deleted record")
    void deleteAircraftReturnsAResponseContainingDataForTheDeletedRecord() throws Exception {
        when(aircraftsDataRepository.findById(anyString()))
                .thenReturn(Optional.of(aircraft1));

        mockMvc.perform(delete("/api/aircraft/" + aircraft1.getAircraftCode()))
               .andExpectAll(
                       jsonPath("$.aircraftCode", is(aircraft1.getAircraftCode())),
                       jsonPath("$.model.en", is(aircraft1.getModel().en())),
                       jsonPath("$.range", is(aircraft1.getRange()))
               );
    }

    @WithMockUser
    @Test
    @DisplayName("Delete aircraft returns an error response when an aircraft record is not found")
    void deleteAircraftReturnsAnErrorResponseWhenAnAircraftRecordIsNotFound() throws Exception {
        when(aircraftsDataRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/aircraft/" + aircraft1.getAircraftCode()))
               .andExpectAll(
                       status().isNotFound(),
                       jsonPath("$.message", containsString(aircraft1.getAircraftCode())),
                       jsonPath("$.statusCode", is(404)),
                       jsonPath("$.cause", is(""))
               );
    }

}