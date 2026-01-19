package com.gardey.tennis_sheet.controllers;

import com.gardey.tennis_sheet.dtos.CreatePersonRequestDTO;
import com.gardey.tennis_sheet.dtos.CreatePersonResponseDTO;
import com.gardey.tennis_sheet.services.PersonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/persons")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    public ResponseEntity<CreatePersonResponseDTO> createPerson(@RequestBody CreatePersonRequestDTO request) {
        CreatePersonResponseDTO response = personService.createPerson(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CreatePersonResponseDTO>> getAllPersons(@RequestParam(required = false) String name) {
        List<CreatePersonResponseDTO> persons;
        if (name != null && !name.trim().isEmpty()) {
            persons = personService.searchPersonsByName(name.trim());
        } else {
            persons = personService.getAllPersons();
        }
        return ResponseEntity.ok(persons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreatePersonResponseDTO> getPersonById(@PathVariable Long id) {
        CreatePersonResponseDTO response = personService.getPersonById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }
}