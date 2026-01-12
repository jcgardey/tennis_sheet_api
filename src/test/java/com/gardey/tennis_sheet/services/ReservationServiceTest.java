package com.gardey.tennis_sheet.services;

import com.gardey.tennis_sheet.dtos.CreateReservationResponseDTO;
import com.gardey.tennis_sheet.exceptions.ResourceNotFoundException;
import com.gardey.tennis_sheet.models.Court;
import com.gardey.tennis_sheet.models.Match;
import com.gardey.tennis_sheet.repositories.CourtRepository;
import com.gardey.tennis_sheet.repositories.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private CourtRepository courtRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Court court;

    @BeforeEach
    void setUp() {
        court = new Court("Center Court");
    }

    @Test
    void getReservationsForCourtOnDate_WhenCourtNotFound_ShouldThrow() {
        when(courtRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> reservationService.getReservationsForCourtOnDate(1L, LocalDate.parse("2026-01-01")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Court not found");
    }

    @Test
    void getReservationsForCourtOnDate_WhenExists_ShouldReturnList() {
        when(courtRepository.existsById(1L)).thenReturn(true);

        LocalDate date = LocalDate.parse("2026-01-01");
        Instant start = date.atTime(LocalTime.of(10, 0)).atZone(java.time.ZoneOffset.UTC).toInstant();
        Instant end = start.plusSeconds(3600);

        Match m = new Match(court, start, end, "Alice", "555");

        when(reservationRepository.findByCourtIdAndStartBetween(eq(1L), any(Instant.class), any(Instant.class)))
            .thenReturn(List.of(m));

        List<CreateReservationResponseDTO> res = reservationService.getReservationsForCourtOnDate(1L, date);

        assertThat(res).hasSize(1);
        CreateReservationResponseDTO dto = res.get(0);
        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getDurationMinutes()).isEqualTo(60);
    }
}
