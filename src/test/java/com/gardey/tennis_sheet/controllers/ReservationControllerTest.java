package com.gardey.tennis_sheet.controllers;

import tools.jackson.databind.ObjectMapper;
import com.gardey.tennis_sheet.dtos.PersonDTO;
import com.gardey.tennis_sheet.dtos.CreateReservationRequestDTO;
import com.gardey.tennis_sheet.dtos.ReservationDTO;
import com.gardey.tennis_sheet.exceptions.ValidationException;
import com.gardey.tennis_sheet.exceptions.ResourceNotFoundException;
import com.gardey.tennis_sheet.models.ReservationType;
import com.gardey.tennis_sheet.services.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createReservation_Match_ShouldReturnCreated() throws Exception {
        // Given
        CreateReservationRequestDTO request = new CreateReservationRequestDTO(
                1L,
                Instant.parse("2026-01-20T10:00:00Z"),
                60L,
                ReservationType.MATCH,
                "Friendly match",
                List.of(1L, 2L),
                null
        );

        ReservationDTO response = new ReservationDTO(
                1L,
                Instant.parse("2026-01-20T10:00:00Z"),
                60L,
                ReservationType.MATCH,
                "Friendly match",
                List.of(
                        new PersonDTO(1L, "John Doe", "john@example.com", "+1234567890"),
                        new PersonDTO(2L, "Jane Smith", "jane@example.com", "+0987654321")
                ),
                null,
                "#FF0000"
        );

        when(reservationService.createReservation(eq(1L), any(CreateReservationRequestDTO.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/courts/1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("MATCH"))
                .andExpect(jsonPath("$.description").value("Friendly match"))
                .andExpect(jsonPath("$.players.length()").value(2))
                .andExpect(jsonPath("$.coach").isEmpty());
    }

    @Test
    void createReservation_Lesson_ShouldReturnCreated() throws Exception {
        // Given
        CreateReservationRequestDTO request = new CreateReservationRequestDTO(
                1L,
                Instant.parse("2026-01-20T10:00:00Z"),
                90L,
                ReservationType.LESSON,
                "Beginner lesson",
                List.of(1L),
                3L
        );

        ReservationDTO response = new ReservationDTO(
                1L,
                Instant.parse("2026-01-20T10:00:00Z"),
                90L,
                ReservationType.LESSON,
                "Beginner lesson",
                List.of(new PersonDTO(1L, "John Doe", "john@example.com", "+1234567890")),
                new PersonDTO(3L, "Coach Mike", "mike@example.com", "+5555555555"),
                "#FF0000"
        );

        when(reservationService.createReservation(eq(1L), any(CreateReservationRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/courts/1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("LESSON"))
                .andExpect(jsonPath("$.description").value("Beginner lesson"))
                .andExpect(jsonPath("$.players.length()").value(1))
                .andExpect(jsonPath("$.coach.name").value("Coach Mike"));
    }

    @Test
    void createReservation_WithValidationError_ShouldReturnBadRequest() throws Exception {
        // Given
        CreateReservationRequestDTO request = new CreateReservationRequestDTO(
                1L,
                Instant.parse("2026-01-20T10:00:00Z"),
                60L,
                ReservationType.MATCH,
                null,
                null,
                null
        );

        when(reservationService.createReservation(eq(1L), any(CreateReservationRequestDTO.class)))
                .thenThrow(new ValidationException("For matches: either players or description is required"));

        // When & Then
        mockMvc.perform(post("/api/courts/1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("For matches: either players or description is required"));
    }

    @Test
    void getReservations_ShouldReturnOk() throws Exception {
        ReservationDTO dto = new ReservationDTO(
                1L,
                Instant.parse("2026-01-01T10:00:00Z"),
                60L,
                ReservationType.MATCH,
                "Morning Match",
                List.of(),
                null,
                "#FF0000"
        );
        when(reservationService.getReservationsForCourtOnDate(anyLong(), any(LocalDate.class)))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/courts/1/reservations").param("date", "2026-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("MATCH"))
                .andExpect(jsonPath("$[0].description").value("Morning Match"));
    }

    @Test
    void getReservations_WhenNotFound_ShouldReturnNotFound() throws Exception {
        when(reservationService.getReservationsForCourtOnDate(anyLong(), any(LocalDate.class)))
                .thenThrow(new ResourceNotFoundException("Court not found with id: 1"));

        mockMvc.perform(get("/api/courts/1/reservations").param("date", "2026-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Court not found with id: 1"));
    }
}
