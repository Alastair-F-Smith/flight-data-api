package com.example.afs.flightdataapi.config;

import com.example.afs.flightdataapi.model.dto.AirportDto;
import com.example.afs.flightdataapi.model.dto.PersonalDetailsDto;
import com.example.afs.flightdataapi.model.dto.Point;
import com.example.afs.flightdataapi.model.entities.Airport;
import com.example.afs.flightdataapi.model.entities.Booking;
import com.example.afs.flightdataapi.services.*;
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

import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest
@Import({SecurityConfig.class, TokenService.class})
class SecurityConfigTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    // Dummy data
    AirportDto airport;

    // Mocked controller dependencies
    @MockBean
    AirportService airportService;
    @MockBean
    AircraftDataService aircraftDataService;
    @MockBean
    BookingService bookingService;
    @MockBean
    FlightService flightService;
    @MockBean
    JourneyService journeyService;
    @MockBean
    SeatService seatService;
    @MockBean
    TicketService ticketService;

    @BeforeEach
    void setUp() {
        airport = new AirportDto("ABC", "Test", "test", new Point(0.0, 0.0), TimeZone.getDefault());
    }

    @Nested
    @DisplayName("Base configuration for API endpoints")
    class baseApiEndpointConfig {

        @Test
        @DisplayName("Get requests require authentication")
        void getRequestsRequireAuthentication() throws Exception {
            mockMvc.perform(get("/api/airports"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        @DisplayName("Get requests are successful when valid credentials are supplied")
        void getRequestsAreSuccessfulWhenValidCredentialsAreSupplied() throws Exception {
            mockMvc.perform(get("/api/airports"))
                   .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        @DisplayName("Post requests by users are forbidden")
        void postRequestsByUsersAreForbidden() throws Exception {
            mockMvc.perform(post("/api/airports")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(airport)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Post requests are allowed for admin users")
        void postRequestsAreAllowedForAdminUsers() throws Exception {
            String json = objectMapper.writeValueAsString(airport);
            when(airportService.fromDto(any(AirportDto.class))).thenReturn(new Airport());
            when(airportService.save(any(Airport.class))).thenReturn(new Airport());
            mockMvc.perform(post("/api/airports")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(json))
                   .andExpect(status().isCreated());
        }

        @Test
        @WithMockUser
        @DisplayName("Put requests by users are forbidden")
        void putRequestsByUsersAreForbidden() throws Exception {
            mockMvc.perform(put("/api/airports/ABC")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(airport)))
                   .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Put requests are allowed for admin users")
        void putRequestsAreAllowedForAdminUsers() throws Exception {
            when(airportService.fromDto(any(AirportDto.class))).thenReturn(new Airport());
            when(airportService.save(any(Airport.class))).thenReturn(new Airport());
            mockMvc.perform(put("/api/airports/ABC")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(airport)))
                   .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        @DisplayName("Delete requests by users are forbidden")
        void deleteRequestsByUsersAreForbidden() throws Exception {
            when(airportService.delete(anyString())).thenReturn(new Airport());
            mockMvc.perform(delete("/api/airports/ABC"))
                   .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Delete requests are allowed for admin users")
        void deleteRequestsAreAllowedForAdminUsers() throws Exception {
            when(airportService.delete(anyString())).thenReturn(new Airport());
            mockMvc.perform(delete("/api/airports/ABC"))
                   .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Booking endpoint security configuration")
    class BookingEndpointConfig {

        @Test
        @WithMockUser
        @DisplayName("Post requests are allowed for authenticated users")
        void postRequestsAreAllowedForAuthenticatedUsers() throws Exception {
            when(bookingService.create()).thenReturn(new Booking());
            mockMvc.perform(post("/api/bookings"))
                    .andExpect(status().isCreated());
        }

        @Test
        @WithMockUser
        @DisplayName("Editing is allowed for authenticated users")
        void editingIsAllowedForAuthenticatedUsers() throws Exception {
            PersonalDetailsDto person = new PersonalDetailsDto("Alice", "alice@email.com", null);
            when(journeyService.toBookingDto(anyString())).thenReturn(null);
            mockMvc.perform(patch("/api/bookings/1234A/tickets/1234567")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(person)))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        @DisplayName("Delete requests are allowed for authenticated users")
        void deleteRequestsAreAllowedForAuthenticatedUsers() throws Exception {
            when(bookingService.delete(anyString())).thenReturn(new Booking());
            mockMvc.perform(delete("/api/bookings/1234A"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Unauthenticated access is not allowed")
        void unauthenticatedAccessIsNotAllowed() throws Exception {
            mockMvc.perform(get("/api/bookings/1234A"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Token endpoint security configuration")
    class TokenEndpointConfig {

        @Test
        @DisplayName("Unauthenticated access is not allowed")
        void unauthenticatedAccessIsNotAllowed() throws Exception {
            mockMvc.perform(post("/api/token"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        @DisplayName("Authenticated user access is permitted")
        void authenticatedUserAccessIsPermitted() throws Exception {
            mockMvc.perform(post("/api/token"))
                   .andExpect(status().isOk());
        }

    }


}