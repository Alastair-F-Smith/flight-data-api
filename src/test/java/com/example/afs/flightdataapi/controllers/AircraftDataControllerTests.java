package com.example.afs.flightdataapi.controllers;

import com.example.afs.flightdataapi.config.HypermediaConfig;
import com.example.afs.flightdataapi.config.SecurityConfig;
import com.example.afs.flightdataapi.controllers.advice.DataAccessAdvice;
import com.example.afs.flightdataapi.model.entities.TranslatedField;
import com.example.afs.flightdataapi.model.entities.AircraftsData;
import com.example.afs.flightdataapi.services.AircraftDataService;
import com.example.afs.flightdataapi.services.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

import static org.hamcrest.Matchers.endsWith;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest({AircraftDataController.class, AuthController.class, DataAccessAdvice.class})
@Import({TokenService.class, SecurityConfig.class, HypermediaConfig.class})
class AircraftDataControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AircraftDataService aircraftDataService;

    AircraftsData aircraft1;
    AircraftsData aircraft2;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        aircraft1 = new AircraftsData("ABC", new TranslatedField("Boeing", "Boeing"), 10000);
        aircraft2 = new AircraftsData("123", new TranslatedField("Airbus", "Airbus"), 15000);
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
        when(aircraftDataService.findById(anyString()))
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
        when(aircraftDataService.findAll())
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

    @WithMockUser(roles = "ADMIN")
    @Test
    @DisplayName("Add aircraft passes correct aircraft data to the repository")
    void addAircraftPassesCorrectAircraftDataToTheRepository() throws Exception {
        String json = objectMapper.writeValueAsString(aircraft1);

        when(aircraftDataService.save(any(AircraftsData.class)))
                .thenReturn(aircraft1);

        mockMvc.perform(post("/api/aircraft")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(json)
                );

        verify(aircraftDataService, times(1)).save(aircraft1);
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    @DisplayName("Add aircraft returns the correct location URL for the newly created resource")
    void addAircraftReturnsTheCorrectLocationUrlForTheNewlyCreatedResource() throws Exception {
        String json = objectMapper.writeValueAsString(aircraft1);

        when(aircraftDataService.save(any(AircraftsData.class)))
                .thenReturn(aircraft1);

        String expectedLocation = "http://localhost/api/aircraft/" + aircraft1.getAircraftCode();

        mockMvc.perform(post("/api/aircraft")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string("location", is(expectedLocation)));
    }

    @WithMockUser
    @Test
    @DisplayName("Add aircraft returns a 403 response status when the user is not an admin")
    void addAircraftReturnsA403ResponseStatusWhenTheUserIsNotAnAdmin() throws Exception {
        String json = objectMapper.writeValueAsString(aircraft1);

        when(aircraftDataService.save(any(AircraftsData.class)))
                .thenReturn(aircraft1);

        mockMvc.perform(post("/api/aircraft")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isForbidden());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    @DisplayName("Update aircraft returns a response containing the updated data")
    void updateAircraftReturnsAResponseContainingTheUpdatedData() throws Exception {
        when(aircraftDataService.findById(anyString()))
                .thenReturn(Optional.of(aircraft1));

        when(aircraftDataService.save(any(AircraftsData.class)))
                .thenAnswer(i -> i.getArgument(0, AircraftsData.class));

        AircraftsData updated = new AircraftsData(aircraft1.getAircraftCode(), new TranslatedField("Airbus", "Airbus"), 123456);

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

    @WithMockUser(roles = "ADMIN")
    @Test
    @DisplayName("Update aircraft returns a not found status code if the ID is not found")
    void updateAircraftReturnsANotFoundStatusCodeIfTheIdIsNotFound() throws Exception {
        when(aircraftDataService.findById(anyString()))
                .thenReturn(Optional.empty());

        when(aircraftDataService.save(any(AircraftsData.class)))
                .thenAnswer(i -> i.getArgument(0, AircraftsData.class));

        AircraftsData updated = new AircraftsData("ZZZ", new TranslatedField("Airbus", "Airbus"), 123456);

        mockMvc.perform(put("/api/aircraft/" + aircraft1.getAircraftCode())
                                .content(objectMapper.writeValueAsString(updated))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    @DisplayName("Update aircraft returns a 403 status code if user is not an admin")
    void updateAircraftReturnsA403StatusCodeIfUserIsNotAnAdmin() throws Exception {
        AircraftsData updated = new AircraftsData("ZZZ", new TranslatedField("Airbus", "Airbus"), 123456);

        mockMvc.perform(put("/api/aircraft/" + aircraft1.getAircraftCode())
                                .content(objectMapper.writeValueAsString(updated))
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isForbidden());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    @DisplayName("Delete aircraft returns 200 status when aircraft record is found")
    void deleteAircraftReturns200StatusWhenAircraftRecordIsFound() throws Exception {
        when(aircraftDataService.findById(anyString()))
                .thenReturn(Optional.of(aircraft1));

        mockMvc.perform(delete("/api/aircraft/" + aircraft1.getAircraftCode()))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    @DisplayName("Delete aircraft returns a response containing data for the deleted record")
    void deleteAircraftReturnsAResponseContainingDataForTheDeletedRecord() throws Exception {
        when(aircraftDataService.findById(anyString()))
                .thenReturn(Optional.of(aircraft1));

        mockMvc.perform(delete("/api/aircraft/" + aircraft1.getAircraftCode()))
               .andExpectAll(
                       jsonPath("$.aircraftCode", is(aircraft1.getAircraftCode())),
                       jsonPath("$.model.en", is(aircraft1.getModel().en())),
                       jsonPath("$.range", is(aircraft1.getRange()))
               );
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    @DisplayName("Delete aircraft returns an error response when an aircraft record is not found")
    void deleteAircraftReturnsAnErrorResponseWhenAnAircraftRecordIsNotFound() throws Exception {
        when(aircraftDataService.findById(anyString()))
                .thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/aircraft/" + aircraft1.getAircraftCode()))
               .andExpectAll(
                       status().isNotFound(),
                       jsonPath("$.message", containsString(aircraft1.getAircraftCode())),
                       jsonPath("$.statusCode", is(404)),
                       jsonPath("$.reason", is(""))
               );
    }

    @WithMockUser
    @Test
    @DisplayName("Delete aircraft returns a 403 status code when user is not an admin")
    void deleteAircraftReturnsA403StatusCodeWhenUserIsNotAnAdmin() throws Exception {
        when(aircraftDataService.findById(anyString()))
                .thenReturn(Optional.of(aircraft1));

        mockMvc.perform(delete("/api/aircraft/" + aircraft1.getAircraftCode()))
               .andExpect(status().isForbidden());
    }

    @Nested
    @DisplayName("Hateoas link rendering")
    class HateoasLinkRendering {

        @WithMockUser
        @Test
        @DisplayName("Get all renders top-level links to self")
        void getAllRendersTopLevelLinksToSelf() throws Exception {
            mockMvc.perform(get("/api/aircraft"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpectAll(
                            jsonPath("$._links.self.href", endsWith("/api/aircraft")),
                            jsonPath("$._templates.default.method", is("POST")),
                            jsonPath("$._templates.default.properties.length()", is(3)),
                            jsonPath("$._templates.default.properties[0].name", is("aircraftCode")),
                            jsonPath("$._templates.default.properties[1].name", is("model")),
                            jsonPath("$._templates.default.properties[2].name", is("range")),
                            jsonPath("$._templates.default.properties[0].minLength", is(3)),
                            jsonPath("$._templates.default.properties[0].maxLength", is(3))
                    );
        }

    }

}