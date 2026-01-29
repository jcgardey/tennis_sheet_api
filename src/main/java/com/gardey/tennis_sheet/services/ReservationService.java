package com.gardey.tennis_sheet.services;

import com.gardey.tennis_sheet.dtos.PersonDTO;
import com.gardey.tennis_sheet.dtos.CreateReservationRequestDTO;
import com.gardey.tennis_sheet.dtos.ReservationDTO;
import com.gardey.tennis_sheet.exceptions.ReservationConflictException;
import com.gardey.tennis_sheet.exceptions.ResourceNotFoundException;
import com.gardey.tennis_sheet.exceptions.ValidationException;
import com.gardey.tennis_sheet.models.*;
import com.gardey.tennis_sheet.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CourtRepository courtRepository;
    private final AppConfigurationRepository configurationRepository;
    private final PlayerProfileRepository playerProfileRepository;
    private final CoachProfileRepository coachProfileRepository;

    public ReservationService(ReservationRepository reservationRepository, CourtRepository courtRepository, 
                             PersonRepository personRepository, AppConfigurationRepository configurationRepository,
                             PlayerProfileRepository playerProfileRepository, CoachProfileRepository coachProfileRepository) {
        this.reservationRepository = reservationRepository;
        this.courtRepository = courtRepository;
        this.configurationRepository = configurationRepository;
        this.playerProfileRepository = playerProfileRepository;
        this.coachProfileRepository = coachProfileRepository;
    }

    @Transactional
    public ReservationDTO createReservation(Long courtId, CreateReservationRequestDTO request) {

        validateBasicReservationData(courtId, request);
        
        if (request.type() == ReservationType.MATCH) {
            validateMatchRules(request);
        } else if (request.type() == ReservationType.LESSON) {
            validateLessonRules(request);
        }
        
        Instant end = request.start().plus(request.durationMinutes(), ChronoUnit.MINUTES);
        if (reservationRepository.existsByCourtIdAndTimeRangeOverlap(courtId, request.start(), end)) {
            throw new ReservationConflictException(courtId, request.start().toString());
        }

        Court court = courtRepository.findById(courtId).get();
        List<PlayerProfile> playerProfiles = getPlayerProfilesByPersonId(request.playerIds());
        CoachProfile coachProfile = request.coachId() != null ? getCoachProfileByPersonId(request.coachId()) : null;
        
        String colorCode = configurationRepository.findValueByKey(AppConfiguration.ConfigurationKey.MATCH_DEFAULT_COLOR_CODE).orElse(null);
        
        Reservation reservation = new Reservation(court, request.start(), end, request.type(), 
                                                 request.description(), playerProfiles, coachProfile, colorCode);
        
        Reservation saved = reservationRepository.save(reservation);
        return mapToResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsForCourtOnDate(Long courtId, LocalDate date) {
        if (!courtRepository.existsById(courtId)) {
            throw new ResourceNotFoundException("Court not found with id: " + courtId);
        }

        Instant startOfDay = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfDay = date.atTime(LocalTime.MAX).plusNanos(1).atZone(ZoneOffset.UTC).toInstant();
        
        List<Reservation> reservations = reservationRepository.findByCourtIdAndStartBetween(courtId, startOfDay, endOfDay);

        return reservations.stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }

    private void validateBasicReservationData(Long courtId, CreateReservationRequestDTO request) {
        if (request.durationMinutes() <= 0) {
            throw new ValidationException("Duration must be positive minutes");
        }
        
        if (!courtRepository.existsById(courtId)) {
            throw new ResourceNotFoundException("Court not found with id: " + courtId);
        }
    }

    private void validateMatchRules(CreateReservationRequestDTO request) {
        boolean hasPlayers = request.playerIds() != null && !request.playerIds().isEmpty();
        boolean hasDescription = request.description() != null && !request.description().trim().isEmpty();
        
        if (!hasPlayers && !hasDescription) {
            throw new ValidationException("For matches: either players or description is required");
        }
        
        if (request.coachId() != null) {
            throw new ValidationException("For matches: coach must be null");
        }
    }

    private void validateLessonRules(CreateReservationRequestDTO request) {
        if (request.coachId() == null) {
            throw new ValidationException("For lessons: coach is required");
        }
        
        if (request.playerIds() == null || request.playerIds().isEmpty()) {
            throw new ValidationException("For lessons: at least one player is required");
        }
    }

    private List<PlayerProfile> getPlayerProfilesByPersonId(List<Long> playerIds) {
        if (playerIds == null || playerIds.isEmpty()) {
            return List.of();
        }
        
        List<PlayerProfile> playerProfiles = playerIds.stream()
                .map(id -> playerProfileRepository.findByPersonId(id).orElse(null))
                .filter(p -> p != null)
                .collect(Collectors.toList());
        if (playerProfiles.size() != playerIds.size()) {
            throw new ResourceNotFoundException("One or more players not found");
        }
        
        return playerProfiles;
    }

    private CoachProfile getCoachProfileByPersonId(Long id) {
        return coachProfileRepository.findByPersonId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coach profile not found with id: " + id));
    }

    private ReservationDTO mapToResponseDTO(Reservation reservation) {
        List<PersonDTO> players = reservation.getPlayers() != null ? 
            reservation.getPlayers().stream()
                .map(p -> new PersonDTO(p.getPerson().getId(), p.getPerson().getName(), p.getPerson().getEmail(), p.getPerson().getPhone()))
                .collect(Collectors.toList()) : List.of();
        
        PersonDTO coach = reservation.getCoachProfile() != null ?
            new PersonDTO(reservation.getCoachProfile().getPerson().getId(), reservation.getCoachProfile().getPerson().getName(),
                                      reservation.getCoachProfile().getPerson().getEmail(), reservation.getCoachProfile().getPerson().getPhone()) : null;
        
        long duration = Duration.between(reservation.getStart(), reservation.getEnd()).toMinutes();
        
        return new ReservationDTO(
            reservation.getId(),
            reservation.getStart(),
            duration,
            reservation.getType(),
            reservation.getDescription(),
            players,
            coach,
            reservation.getColorCode()
        );
    }
}
