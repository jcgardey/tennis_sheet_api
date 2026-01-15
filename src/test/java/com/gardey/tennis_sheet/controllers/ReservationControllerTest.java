package com.gardey.tennis_sheet.controllers;

import com.gardey.tennis_sheet.dtos.CreateReservationResponseDTO;
import com.gardey.tennis_sheet.exceptions.ResourceNotFoundException;
import com.gardey.tennis_sheet.services.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    void getReservations_ShouldReturnOk() throws Exception {
        CreateReservationResponseDTO dto = new CreateReservationResponseDTO(1L, Instant.parse("2026-01-01T10:00:00Z"), 60, "Morning Match", "primary-blue");
        when(reservationService.getReservationsForCourtOnDate(anyLong(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/courts/1/reservations").param("date", "2026-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].start").value("2026-01-01T10:00:00.000+0000"))
                .andExpect(jsonPath("$[0].description").value("Morning Match"));
    }

    @Test
    void getReservations_WhenNotFound_ShouldReturnNotFound() throws Exception {
        when(reservationService.getReservationsForCourtOnDate(anyLong(), org.mockito.ArgumentMatchers.any()))
                .thenThrow(new ResourceNotFoundException("Court not found with id: 1"));

        mockMvc.perform(get("/api/courts/1/reservations").param("date", "2026-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Court not found with id: 1"));
    }
}
