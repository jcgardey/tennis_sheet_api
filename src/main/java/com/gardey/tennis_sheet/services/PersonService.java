package com.gardey.tennis_sheet.services;

import com.gardey.tennis_sheet.dtos.CreatePersonRequestDTO;
import com.gardey.tennis_sheet.dtos.PersonDTO;
import com.gardey.tennis_sheet.exceptions.PersonEmailAlreadyExistsException;
import com.gardey.tennis_sheet.exceptions.ResourceNotFoundException;
import com.gardey.tennis_sheet.models.CoachProfile;
import com.gardey.tennis_sheet.models.Person;
import com.gardey.tennis_sheet.models.PlayerProfile;
import com.gardey.tennis_sheet.models.ProfileType;
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
    public PersonDTO createPerson(CreatePersonRequestDTO request) {
        Person existingPerson = personRepository.findByEmailIgnoreCase(request.email())
                .orElse(null);
        if (existingPerson != null) {
            throw new PersonEmailAlreadyExistsException(request.email());
        }
        Person person = new Person(request.name(), request.email(), request.phone());
        person.setPlayerProfile(new PlayerProfile(person));
        if (request.isCoach()) {
            person.setCoachProfile(new CoachProfile(person, request.colorCode(), request.hourlyRate()));
        }
        Person saved = personRepository.save(person);
        return mapToResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public PersonDTO getPersonById(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + id));
        return mapToResponseDTO(person);
    }

    @Transactional(readOnly = true)
    public List<PersonDTO> getAllPersonsByProfileAndName(ProfileType profile, String name) {
        List <Person> persons = profile == ProfileType.COACH ?
                personRepository.findAllCoachesByName(name) :
                personRepository.findAllPlayersByName(name);
        return persons.stream()
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

    private PersonDTO mapToResponseDTO(Person person) {
        return new PersonDTO(
                person.getId(),
                person.getName(),
                person.getEmail(),
                person.getPhone()
        );
    }
}