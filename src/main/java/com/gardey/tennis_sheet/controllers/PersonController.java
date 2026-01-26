package com.gardey.tennis_sheet.controllers;

import com.gardey.tennis_sheet.dtos.CreatePersonRequestDTO;
import com.gardey.tennis_sheet.dtos.PersonDTO;
import com.gardey.tennis_sheet.models.ProfileType;
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
    public ResponseEntity<PersonDTO> createPerson(@RequestBody CreatePersonRequestDTO request) {
        PersonDTO response = personService.createPerson(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PersonDTO>> getAllPersons(
        @RequestParam(required = false, defaultValue = "PLAYER") ProfileType profile,
        @RequestParam(required = false, defaultValue = "") String name) {
        List<PersonDTO> persons;
        
        persons = personService.getAllPersonsByProfileAndName(profile, name.trim());
       
        return ResponseEntity.ok(persons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDTO> getPersonById(@PathVariable Long id) {
        PersonDTO response = personService.getPersonById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }
}