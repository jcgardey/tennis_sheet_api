package com.gardey.tennis_sheet.services;

import com.gardey.tennis_sheet.dtos.CreatePersonRequestDTO;
import com.gardey.tennis_sheet.dtos.CreatePersonResponseDTO;
import com.gardey.tennis_sheet.exceptions.ResourceNotFoundException;
import com.gardey.tennis_sheet.models.Person;
import com.gardey.tennis_sheet.repositories.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Transactional
    public CreatePersonResponseDTO createPerson(CreatePersonRequestDTO request) {
        Person person = new Person(request.getName(), request.getEmail(), request.getPhone());
        Person saved = personRepository.save(person);
        return mapToResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<CreatePersonResponseDTO> getAllPersons() {
        return personRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CreatePersonResponseDTO getPersonById(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + id));
        return mapToResponseDTO(person);
    }

    @Transactional(readOnly = true)
    public List<CreatePersonResponseDTO> searchPersonsByName(String name) {
        return personRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePerson(Long id) {
        if (!personRepository.existsById(id)) {
            throw new ResourceNotFoundException("Person not found with id: " + id);
        }
        personRepository.deleteById(id);
    }

    private CreatePersonResponseDTO mapToResponseDTO(Person person) {
        return new CreatePersonResponseDTO(
                person.getId(),
                person.getName(),
                person.getEmail(),
                person.getPhone()
        );
    }
}