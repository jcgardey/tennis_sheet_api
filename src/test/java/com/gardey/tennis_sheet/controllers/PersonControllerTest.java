package com.gardey.tennis_sheet.controllers;

import tools.jackson.databind.ObjectMapper;
import com.gardey.tennis_sheet.dtos.CreatePersonRequestDTO;
import com.gardey.tennis_sheet.dtos.PersonDTO;
import com.gardey.tennis_sheet.exceptions.PersonEmailAlreadyExistsException;
import com.gardey.tennis_sheet.exceptions.ResourceNotFoundException;
import com.gardey.tennis_sheet.models.ProfileType;
import com.gardey.tennis_sheet.services.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonService personService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createPerson_ShouldReturnCreated() throws Exception {
        CreatePersonRequestDTO request = new CreatePersonRequestDTO("John Doe", "john@example.com", "+1234567890", false, null, null);
        PersonDTO response = new PersonDTO(1L, "John Doe", "john@example.com", "+1234567890");
        
        when(personService.createPerson(any(CreatePersonRequestDTO.class))).thenReturn(response);
        
        mockMvc.perform(post("/api/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.phone").value("+1234567890"));
    }

    @Test
    void createPerson_WhenEmailExists_ShouldReturnBadRequest() throws Exception {
        CreatePersonRequestDTO request = new CreatePersonRequestDTO("", "invalid@email.com", "123", false, null, null);
        when(personService.createPerson(any(CreatePersonRequestDTO.class)))
        .thenThrow(new PersonEmailAlreadyExistsException("invalid@email.com"));
        mockMvc.perform(post("/api/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already exists: " + request.email()));
    }

    @Test
    void getAllPersons_ShouldReturnList() throws Exception {
        List<PersonDTO> persons = List.of(
                new PersonDTO(1L, "John Doe", "john@example.com", "+1234567890"),
                new PersonDTO(2L, "Jane Smith", "jane@example.com", "+0987654321")
        );
        
        when(personService.getAllPersonsByProfileAndName(ProfileType.PLAYER, "")).thenReturn(persons);
        
        mockMvc.perform(get("/api/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"));
    }

    @Test
    void getPersonById_ShouldReturnPerson() throws Exception {
        PersonDTO response = new PersonDTO(1L, "John Doe", "john@example.com", "+1234567890");
        
        when(personService.getPersonById(1L)).thenReturn(response);
        
        mockMvc.perform(get("/api/persons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void getPersonById_ShouldReturnNotFound() throws Exception {
        when(personService.getPersonById(999L)).thenThrow(new ResourceNotFoundException("Person not found with id: 999"));
        
        mockMvc.perform(get("/api/persons/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePerson_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/persons/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePerson_ShouldReturnNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Person not found with id: 999")).when(personService).deletePerson(999L);
        
        mockMvc.perform(delete("/api/persons/999"))
                .andExpect(status().isNotFound());
    }
}