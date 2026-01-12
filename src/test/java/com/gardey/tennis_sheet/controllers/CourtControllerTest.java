package com.gardey.tennis_sheet.controllers;

import tools.jackson.databind.ObjectMapper;
import com.gardey.tennis_sheet.dtos.CreateCourtRequestDTO;
import com.gardey.tennis_sheet.dtos.CreateCourtResponseDTO;
import com.gardey.tennis_sheet.services.CourtService;
import com.gardey.tennis_sheet.exceptions.CourtNameAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourtController.class)
class CourtControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourtService courtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void list_ShouldReturnOk() throws Exception {
        CreateCourtResponseDTO response = new CreateCourtResponseDTO(1L, "Court 1");
        when(courtService.getAllCourts()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/courts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Court 1"));
    }

    @Test
    void create_ShouldReturnCreated() throws Exception {
        CreateCourtRequestDTO request = new CreateCourtRequestDTO("New Court");
        CreateCourtResponseDTO response = new CreateCourtResponseDTO(1L, "New Court");
        
        when(courtService.createCourt(any(CreateCourtRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/courts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Court"));
    }

        @Test
        void create_WhenNameExists_ShouldReturnBadRequestWithErrorResponse() throws Exception {
        CreateCourtRequestDTO request = new CreateCourtRequestDTO("Center Court");

        when(courtService.createCourt(any(CreateCourtRequestDTO.class)))
            .thenThrow(new CourtNameAlreadyExistsException("Center Court"));

        mockMvc.perform(post("/api/courts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Court with name 'Center Court' already exists"));
        }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/courts/1"))
                .andExpect(status().isNoContent());
    }
}