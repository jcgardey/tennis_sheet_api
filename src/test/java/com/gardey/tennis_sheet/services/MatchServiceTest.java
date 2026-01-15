package com.gardey.tennis_sheet.services;

import com.gardey.tennis_sheet.dtos.CreateMatchRequestDTO;
import com.gardey.tennis_sheet.dtos.CreateMatchResponseDTO;
import com.gardey.tennis_sheet.exceptions.ReservationConflictException;
import com.gardey.tennis_sheet.exceptions.ResourceNotFoundException;
import com.gardey.tennis_sheet.models.AppConfiguration;
import com.gardey.tennis_sheet.models.Court;
import com.gardey.tennis_sheet.models.Match;
import com.gardey.tennis_sheet.models.Reservation;
import com.gardey.tennis_sheet.repositories.AppConfigurationRepository;
import com.gardey.tennis_sheet.repositories.CourtRepository;
import com.gardey.tennis_sheet.repositories.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private CourtRepository courtRepository;

    @Mock
    private AppConfigurationRepository configurationRepository;

    @InjectMocks
    private MatchService matchService;

    private Court court;

    @BeforeEach
    void setUp() {
        court = new Court("Center Court");
    }

    @Test
    void createMatch_WhenCourtNotFound_ShouldThrow() {
        when(courtRepository.existsById(1L)).thenReturn(false);

        CreateMatchRequestDTO req = new CreateMatchRequestDTO(1L, Instant.parse("2026-01-01T10:00:00Z"), 60, "Alice", "555");

            assertThatThrownBy(() -> matchService.createMatch(1L, req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Court not found");
    }

    @Test
    void createMatch_WhenOverlap_ShouldThrowConflict() {
        when(courtRepository.existsById(1L)).thenReturn(true);
        when(reservationRepository.existsByCourtIdAndTimeRangeOverlap(anyLong(), any(Instant.class), any(Instant.class))).thenReturn(true);

        CreateMatchRequestDTO req = new CreateMatchRequestDTO(1L, Instant.parse("2026-01-01T10:00:00Z"), 60, "Alice", "555");

            assertThrows(ReservationConflictException.class, () -> matchService.createMatch(1L, req));
    }

    @Test
    void createMatch_WhenValid_ShouldSaveAndReturnResponse() throws ReservationConflictException {
        when(courtRepository.existsById(1L)).thenReturn(true);
        when(reservationRepository.existsByCourtIdAndTimeRangeOverlap(anyLong(), any(Instant.class), any(Instant.class))).thenReturn(false);
        when(configurationRepository.findValueByKey(AppConfiguration.ConfigurationKey.MATCH_DEFAULT_COLOR_CODE)).thenReturn(Optional.of("primary"));

        when(courtRepository.findById(1L)).thenReturn(Optional.of(court));

        Instant start = Instant.parse("2026-01-01T10:00:00Z");
        Instant end = start.plusSeconds(60 * 60);
        Match saved = new Match(court, start, end, "Alice", "555", "primary");

        when(reservationRepository.save(any(Reservation.class))).thenReturn(saved);

        CreateMatchRequestDTO req = new CreateMatchRequestDTO(1L, Instant.parse("2026-01-01T10:00:00Z"), 60, "Alice", "555");

        CreateMatchResponseDTO resp = matchService.createMatch(1L, req);

        assertThat(resp.getPlayerName()).isEqualTo("Alice");
        assertThat(resp.getContactPhone()).isEqualTo("555");
        assertThat(resp.getStart()).isEqualTo("2026-01-01T10:00:00Z");
        assertThat(resp.getDurationMinutes()).isEqualTo(60);
        assertThat(resp.getColorCode()).isEqualTo("primary");

        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }
}
