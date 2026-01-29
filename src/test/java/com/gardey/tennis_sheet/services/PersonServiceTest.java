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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    private Person playerJohn;
    private Person coachTeddy;

    @BeforeEach
    void setUp() {
        playerJohn = new Person(1L, "John Doe", "john@example.com", "+1234567890");
        playerJohn.setPlayerProfile(new PlayerProfile());
        
        coachTeddy = new Person(2L, "Teddy Coach", "teddy@example.com", "+0987654321");
        coachTeddy.setCoachProfile(new CoachProfile(coachTeddy, null, null));
    }

    @Test
    void createPerson_ShouldReturnResponseDTO() {
        CreatePersonRequestDTO request = new CreatePersonRequestDTO("John Doe", "john@example.com", "+1234567890", false, null, null);
        
        when(personRepository.save(any(Person.class))).thenReturn(playerJohn);

        PersonDTO result = personService.createPerson(request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("John Doe");
        assertThat(result.email()).isEqualTo("john@example.com");
        assertThat(result.phone()).isEqualTo("+1234567890");
        verify(personRepository).save(any(Person.class));
    }

    @Test
    void createPerson_WhenEmailAlreadyExists_ShouldThrowException() {
        CreatePersonRequestDTO request = new CreatePersonRequestDTO("John Doe", "john@example.com", "+1234567890", false, null, null);
        
        when(personRepository.findByEmailIgnoreCase("john@example.com")).thenReturn(Optional.of(playerJohn));

        assertThatThrownBy(() -> personService.createPerson(request))
                .isInstanceOf(PersonEmailAlreadyExistsException.class)
                .hasMessage("Email already exists: john@example.com");
        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    void getPersonById_WhenPersonExists_ShouldReturnResponseDTO() {
        when(personRepository.findById(1L)).thenReturn(Optional.of(playerJohn));

        PersonDTO result = personService.getPersonById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("John Doe");
        verify(personRepository).findById(1L);
    }

    @Test
    void getPersonById_WhenPersonNotFound_ShouldThrowException() {
        when(personRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> personService.getPersonById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Person not found with id: 999");
        verify(personRepository).findById(999L);
    }

    @Test
    void getAllPersonsByProfileAndName_ShouldReturnMatchingPlayers() {
        List<Person> players = List.of(playerJohn);
        List<Person> coaches = List.of(coachTeddy);

        when(personRepository.findAllPlayersByName("John")).thenReturn(players);
        when(personRepository.findAllCoachesByName("Teddy")).thenReturn(coaches);

        List<PersonDTO> result = personService.getAllPersonsByProfileAndName(ProfileType.PLAYER, "John");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("John Doe");
        verify(personRepository).findAllPlayersByName("John");
        
        List<PersonDTO> resultCoaches = personService.getAllPersonsByProfileAndName(ProfileType.COACH, "Teddy");
        assertThat(resultCoaches).hasSize(1);
        assertThat(resultCoaches.get(0).name()).isEqualTo("Teddy Coach");
        verify(personRepository).findAllCoachesByName("Teddy");
    }

    @Test
    void deletePerson_WhenPersonExists_ShouldDeleteSuccessfully() {
        when(personRepository.existsById(1L)).thenReturn(true);

        personService.deletePerson(1L);

        verify(personRepository).existsById(1L);
        verify(personRepository).deleteById(1L);
    }

    @Test
    void deletePerson_WhenPersonNotFound_ShouldThrowException() {
        when(personRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> personService.deletePerson(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Person not found with id: 999");
        verify(personRepository).existsById(999L);
        verify(personRepository, never()).deleteById(any());
    }

}