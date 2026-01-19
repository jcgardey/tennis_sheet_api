package com.gardey.tennis_sheet.services;

import com.gardey.tennis_sheet.dtos.CreatePersonRequestDTO;
import com.gardey.tennis_sheet.dtos.CreatePersonResponseDTO;
import com.gardey.tennis_sheet.exceptions.ResourceNotFoundException;
import com.gardey.tennis_sheet.models.Person;
import com.gardey.tennis_sheet.repositories.PersonRepository;
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

    @Test
    void createPerson_ShouldReturnResponseDTO() {
        CreatePersonRequestDTO request = new CreatePersonRequestDTO("John Doe", "john@example.com", "+1234567890");
        Person savedPerson = new Person(1L, "John Doe", "john@example.com", "+1234567890");
        
        when(personRepository.save(any(Person.class))).thenReturn(savedPerson);

        CreatePersonResponseDTO result = personService.createPerson(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        assertThat(result.getPhone()).isEqualTo("+1234567890");
        verify(personRepository).save(any(Person.class));
    }

    @Test
    void getAllPersons_ShouldReturnList() {
        List<Person> persons = List.of(
            new Person(1L, "John Doe", "john@example.com", "+1234567890"),
            new Person(2L,"Jane Smith", "jane@example.com", "+0987654321")
        );

        when(personRepository.findAll()).thenReturn(persons);

        List<CreatePersonResponseDTO> result = personService.getAllPersons();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
        assertThat(result.get(1).getName()).isEqualTo("Jane Smith");
        verify(personRepository).findAll();
    }

    @Test
    void getPersonById_WhenPersonExists_ShouldReturnResponseDTO() {
        Person person = new Person(1L, "John Doe", "john@example.com", "+1234567890");
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        CreatePersonResponseDTO result = personService.getPersonById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
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
    void searchPersonsByName_ShouldReturnMatchingPersons() {
        List<Person> persons = List.of(
            new Person(1L, "John Doe", "john@example.com", "+1234567890")
        );

        when(personRepository.findByNameContainingIgnoreCase("John")).thenReturn(persons);

        List<CreatePersonResponseDTO> result = personService.searchPersonsByName("John");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
        verify(personRepository).findByNameContainingIgnoreCase("John");
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