package com.gardey.tennis_sheet.services;


import com.gardey.tennis_sheet.dtos.CreateCourtRequestDTO;
import com.gardey.tennis_sheet.dtos.CreateCourtResponseDTO;
import com.gardey.tennis_sheet.exceptions.CourtNameAlreadyExistsException;
import com.gardey.tennis_sheet.models.Court;
import com.gardey.tennis_sheet.repositories.CourtRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourtServiceTest {

    @Mock
    private CourtRepository courtRepository;

    @InjectMocks
    private CourtService courtService;

    private Court court;

    @BeforeEach
    void setUp() {
        court = new Court("Center Court");
    }

    @Test
    void getAllCourts_ShouldReturnList() {
        when(courtRepository.findAll()).thenReturn(List.of(court));

        List<CreateCourtResponseDTO> result = courtService.getAllCourts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Center Court");
        verify(courtRepository, times(1)).findAll();
    }

    @Test
    void createCourt_WhenNameExists_ShouldThrowException() {
        CreateCourtRequestDTO request = new CreateCourtRequestDTO("Center Court");
        when(courtRepository.existsByName("Center Court")).thenReturn(true);

        assertThatThrownBy(() -> courtService.createCourt(request))
            .isInstanceOf(CourtNameAlreadyExistsException.class)
            .hasMessageContaining("already exists");
        
        verify(courtRepository, never()).save(any());
    }

    @Test
    void createCourt_WhenNameIsNew_ShouldSaveAndReturnResponse() throws CourtNameAlreadyExistsException {
        CreateCourtRequestDTO request = new CreateCourtRequestDTO("New Court");
        when(courtRepository.existsByName("New Court")).thenReturn(false);
        when(courtRepository.save(any(Court.class))).thenReturn(new Court("New Court"));

        CreateCourtResponseDTO result = courtService.createCourt(request);
        assertThat(result.getName()).isEqualTo("New Court");
        verify(courtRepository, times(1)).save(any());
    }

    @Test
    void deleteCourt_WhenIdDoesNotExist_ShouldThrowException() {
        when(courtRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> courtService.deleteCourt(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void deleteCourt_WhenIdExists_ShouldDelete() {
        when(courtRepository.existsById(1L)).thenReturn(true);

        courtService.deleteCourt(1L);

        verify(courtRepository, times(1)).deleteById(1L);
    }
}
