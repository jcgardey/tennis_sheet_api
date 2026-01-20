package com.gardey.tennis_sheet.services;

import com.gardey.tennis_sheet.dtos.CreateReservationRequestDTO;
import com.gardey.tennis_sheet.dtos.ReservationDTO;
import com.gardey.tennis_sheet.exceptions.ReservationConflictException;
import com.gardey.tennis_sheet.exceptions.ValidationException;
import com.gardey.tennis_sheet.exceptions.ResourceNotFoundException;
import com.gardey.tennis_sheet.models.*;
import com.gardey.tennis_sheet.repositories.AppConfigurationRepository;
import com.gardey.tennis_sheet.repositories.CourtRepository;
import com.gardey.tennis_sheet.repositories.PersonRepository;
import com.gardey.tennis_sheet.repositories.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private CourtRepository courtRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private AppConfigurationRepository configurationRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Court court;
    private Person player1;
    private Person coach;

    @BeforeEach
    void setUp() {
        court = new Court(1L, "Center Court");
        player1 = new Person(1L, "John Doe", "john@example.com", "+1234567890");
        coach = new Person(3L, "Coach Mike", "mike@example.com", "+5555555555");
    }

    @Test
    void createReservation_Match_WithDescriptionOnly_ShouldSucceed() throws Exception {
        // Given
        CreateReservationRequestDTO request = new CreateReservationRequestDTO(
                1L,
                Instant.parse("2026-01-20T10:00:00Z"),
                60L,
                ReservationType.MATCH,
                "Singles practice",
                null,
                null
        );

        when(courtRepository.existsById(1L)).thenReturn(true);
        when(courtRepository.findById(1L)).thenReturn(Optional.of(court));
        when(reservationRepository.existsByCourtIdAndTimeRangeOverlap(any(), any(), any())).thenReturn(false);
        when(configurationRepository.findValueByKey(AppConfiguration.ConfigurationKey.MATCH_DEFAULT_COLOR_CODE))
                .thenReturn(Optional.of("#FF0000"));
        
        Reservation savedReservation = new Reservation(
                1L,
                court,
                request.getStart(),
                request.getStart().plusSeconds(request.getDurationMinutes() * 60),
                request.getType(),
                request.getDescription(),
                List.of(),
                null,
                "#FF0000"
        );
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

        ReservationDTO result = reservationService.createReservation(1L, request);

        assertThat(result.getType()).isEqualTo(ReservationType.MATCH);
        assertThat(result.getDescription()).isEqualTo("Singles practice");
        assertThat(result.getPlayers()).isEmpty();
        assertThat(result.getCoach()).isNull();
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void createReservation_Match_WithCoach_ShouldFail() {
        CreateReservationRequestDTO request = new CreateReservationRequestDTO(
                1L,
                Instant.parse("2026-01-20T10:00:00Z"),
                60L,
                ReservationType.MATCH,
                "Match with coach",
                List.of(1L),
                3L // Coach not allowed for matches
        );

        when(courtRepository.existsById(1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> reservationService.createReservation(1L, request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("For matches: coach must be null");
    }

    @Test
    void createReservation_Match_WithoutPlayersOrDescription_ShouldFail() {
        CreateReservationRequestDTO request = new CreateReservationRequestDTO(
                1L,
                Instant.parse("2026-01-20T10:00:00Z"),
                60L,
                ReservationType.MATCH,
                null,
                null,
                null
        );

        when(courtRepository.existsById(1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> reservationService.createReservation(1L, request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("For matches: either players or description is required");
    }

    @Test
    void createReservation_Lesson_WithCoachAndPlayers_ShouldSucceed() throws Exception {
        // Given
        CreateReservationRequestDTO request = new CreateReservationRequestDTO(
                1L,
                Instant.parse("2026-01-20T10:00:00Z"),
                90L,
                ReservationType.LESSON,
                "Beginner lesson",
                List.of(1L),
                3L
        );

        when(courtRepository.existsById(1L)).thenReturn(true);
        when(courtRepository.findById(1L)).thenReturn(Optional.of(court));
        when(personRepository.findAllById(List.of(1L))).thenReturn(List.of(player1));
        when(personRepository.findById(3L)).thenReturn(Optional.of(coach));
        when(reservationRepository.existsByCourtIdAndTimeRangeOverlap(any(), any(), any())).thenReturn(false);
        when(configurationRepository.findValueByKey(AppConfiguration.ConfigurationKey.MATCH_DEFAULT_COLOR_CODE))
                .thenReturn(Optional.of("#FF0000"));
        
        Reservation savedReservation = new Reservation(
                1L,
                court,
                request.getStart(),
                request.getStart().plusSeconds(request.getDurationMinutes() * 60),
                request.getType(),
                request.getDescription(),
                List.of(player1),
                coach,
                "#FF0000"
        );
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

        ReservationDTO result = reservationService.createReservation(1L, request);

        assertThat(result.getType()).isEqualTo(ReservationType.LESSON);
        assertThat(result.getDescription()).isEqualTo("Beginner lesson");
        assertThat(result.getPlayers()).hasSize(1);
        assertThat(result.getCoach()).isNotNull();
        assertThat(result.getCoach().getName()).isEqualTo("Coach Mike");
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void createReservation_Lesson_WithoutCoach_ShouldFail() {
        CreateReservationRequestDTO request = new CreateReservationRequestDTO(
                1L,
                Instant.parse("2026-01-20T10:00:00Z"),
                90L,
                ReservationType.LESSON,
                "Lesson without coach",
                List.of(1L),
                null // Coach required for lessons
        );

        when(courtRepository.existsById(1L)).thenReturn(true);

        assertThatThrownBy(() -> reservationService.createReservation(1L, request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("For lessons: coach is required");
    }

    @Test
    void createReservation_Lesson_WithoutPlayers_ShouldFail() {
        // Given
        CreateReservationRequestDTO request = new CreateReservationRequestDTO(
                1L,
                Instant.parse("2026-01-20T10:00:00Z"),
                90L,
                ReservationType.LESSON,
                "Lesson without players",
                null, // Players required for lessons
                3L
        );

        when(courtRepository.existsById(1L)).thenReturn(true);

        assertThatThrownBy(() -> reservationService.createReservation(1L, request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("For lessons: at least one player is required");
    }

    @Test
    void createReservation_WhenTimeConflicts_ShouldThrowConflictException() {
        CreateReservationRequestDTO request = new CreateReservationRequestDTO(
                1L,
                Instant.parse("2026-01-20T10:00:00Z"),
                60L,
                ReservationType.MATCH,
                "Conflicting match",
                null,
                null
        );

        when(courtRepository.existsById(1L)).thenReturn(true);
        when(reservationRepository.existsByCourtIdAndTimeRangeOverlap(any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> reservationService.createReservation(1L, request))
                .isInstanceOf(ReservationConflictException.class);
    }

    @Test
    void getReservationsForCourtOnDate_WhenCourtNotFound_ShouldThrowResourceNotFoundException() {
        when(courtRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> reservationService.getReservationsForCourtOnDate(1L, LocalDate.parse("2026-01-20")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Court not found with id: 1");
    }

    @Test
    void getReservationsForCourtOnDate_ShouldReturnReservations() {
        // Given
        when(courtRepository.existsById(1L)).thenReturn(true);

        
        Reservation reservation = new Reservation(
            1L,
            court,
            Instant.parse("2026-01-20T10:00:00Z"),
            Instant.parse("2026-01-20T11:00:00Z"),
            ReservationType.MATCH,
            "Test match",
            List.of(),
            null,
            "#FF0000"
        );
        
        when(reservationRepository.findByCourtIdAndStartBetween(eq(1L), any(), any()))
                .thenReturn(List.of(reservation));

        // When
        List<ReservationDTO> result = reservationService.getReservationsForCourtOnDate(1L, LocalDate.parse("2026-01-20"));

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getType()).isEqualTo(ReservationType.MATCH);
        assertThat(result.get(0).getDescription()).isEqualTo("Test match");
    }

}
