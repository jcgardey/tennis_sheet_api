package com.gardey.tennis_sheet.controllers;

import tools.jackson.databind.ObjectMapper;
import com.gardey.tennis_sheet.dtos.CreateMatchRequestDTO;
import com.gardey.tennis_sheet.dtos.CreateMatchResponseDTO;
import com.gardey.tennis_sheet.exceptions.ReservationConflictException;
import com.gardey.tennis_sheet.services.MatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

@WebMvcTest(MatchController.class)
class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MatchService matchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_ShouldReturnCreated() throws Exception {
        Instant start = Instant.parse("2026-01-01T10:00:00.000Z");
        CreateMatchRequestDTO request = new CreateMatchRequestDTO(1L, start, 60, "Alice", "555");
        CreateMatchResponseDTO response = new CreateMatchResponseDTO(1L, start, 60, "Alice", "555");

            when(matchService.createMatch(anyLong(), any(CreateMatchRequestDTO.class))).thenReturn(response);

            mockMvc.perform(post("/api/courts/1/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.playerName").value("Alice"))
                .andExpect(jsonPath("$.contactPhone").value("555"))
                .andExpect(jsonPath("$.durationMinutes").value(60));
    }

    @Test
    void create_WhenConflict_ShouldReturnBadRequest() throws Exception {
        CreateMatchRequestDTO request = new CreateMatchRequestDTO(1L, Instant.parse("2026-01-01T10:00:00.00Z"), 60, "Alice", "555");

            when(matchService.createMatch(anyLong(), any(CreateMatchRequestDTO.class)))
                .thenThrow(new ReservationConflictException(1L, "2026-01-01T10:00"));

            mockMvc.perform(post("/api/courts/1/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Reservation conflict for court 1 at 2026-01-01T10:00"));
    }
}
