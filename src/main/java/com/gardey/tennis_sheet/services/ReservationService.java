package com.gardey.tennis_sheet.services;

import com.gardey.tennis_sheet.dtos.PersonDTO;
import com.gardey.tennis_sheet.dtos.CreateReservationRequestDTO;
import com.gardey.tennis_sheet.dtos.ReservationDTO;
import com.gardey.tennis_sheet.exceptions.ReservationConflictException;
import com.gardey.tennis_sheet.exceptions.ResourceNotFoundException;
import com.gardey.tennis_sheet.exceptions.ValidationException;
import com.gardey.tennis_sheet.models.*;
import com.gardey.tennis_sheet.repositories.AppConfigurationRepository;
import com.gardey.tennis_sheet.repositories.CourtRepository;
import com.gardey.tennis_sheet.repositories.PersonRepository;
import com.gardey.tennis_sheet.repositories.ReservationRepository;
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
    private final PersonRepository personRepository;
    private final AppConfigurationRepository configurationRepository;

    public ReservationService(ReservationRepository reservationRepository, CourtRepository courtRepository, 
                             PersonRepository personRepository, AppConfigurationRepository configurationRepository) {
        this.reservationRepository = reservationRepository;
        this.courtRepository = courtRepository;
        this.personRepository = personRepository;
        this.configurationRepository = configurationRepository;
    }

    @Transactional
    public ReservationDTO createReservation(Long courtId, CreateReservationRequestDTO request) {

        validateBasicReservationData(courtId, request);
        
        if (request.getType() == ReservationType.MATCH) {
            validateMatchRules(request);
        } else if (request.getType() == ReservationType.LESSON) {
            validateLessonRules(request);
        }
        
        Instant end = request.getStart().plus(request.getDurationMinutes(), ChronoUnit.MINUTES);
        if (reservationRepository.existsByCourtIdAndTimeRangeOverlap(courtId, request.getStart(), end)) {
            throw new ReservationConflictException(courtId, request.getStart().toString());
        }

        Court court = courtRepository.findById(courtId).get();
        List<Person> players = getPlayersById(request.getPlayerIds());
        Person coach = request.getCoachId() != null ? getPersonById(request.getCoachId()) : null;
        
        String colorCode = configurationRepository.findValueByKey(AppConfiguration.ConfigurationKey.MATCH_DEFAULT_COLOR_CODE).orElse(null);
        
        Reservation reservation = new Reservation(court, request.getStart(), end, request.getType(), 
                                                 request.getDescription(), players, coach, colorCode);
        
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
        if (request.getDurationMinutes() <= 0) {
            throw new ValidationException("Duration must be positive minutes");
        }
        
        if (!courtRepository.existsById(courtId)) {
            throw new ResourceNotFoundException("Court not found with id: " + courtId);
        }
    }

    private void validateMatchRules(CreateReservationRequestDTO request) {
        boolean hasPlayers = request.getPlayerIds() != null && !request.getPlayerIds().isEmpty();
        boolean hasDescription = request.getDescription() != null && !request.getDescription().trim().isEmpty();
        
        if (!hasPlayers && !hasDescription) {
            throw new ValidationException("For matches: either players or description is required");
        }
        
        if (request.getCoachId() != null) {
            throw new ValidationException("For matches: coach must be null");
        }
    }

    private void validateLessonRules(CreateReservationRequestDTO request) {
        if (request.getCoachId() == null) {
            throw new ValidationException("For lessons: coach is required");
        }
        
        if (request.getPlayerIds() == null || request.getPlayerIds().isEmpty()) {
            throw new ValidationException("For lessons: at least one player is required");
        }
    }

    private List<Person> getPlayersById(List<Long> playerIds) {
        if (playerIds == null || playerIds.isEmpty()) {
            return List.of();
        }
        
        List<Person> players = personRepository.findAllById(playerIds);
        if (players.size() != playerIds.size()) {
            throw new ResourceNotFoundException("One or more players not found");
        }
        
        return players;
    }

    private Person getPersonById(Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + id));
    }

    private ReservationDTO mapToResponseDTO(Reservation reservation) {
        List<PersonDTO> players = reservation.getPlayers() != null ? 
            reservation.getPlayers().stream()
                .map(p -> new PersonDTO(p.getId(), p.getName(), p.getEmail(), p.getPhone()))
                .collect(Collectors.toList()) : List.of();
        
        PersonDTO coach = reservation.getCoach() != null ?
            new PersonDTO(reservation.getCoach().getId(), reservation.getCoach().getName(),
                                      reservation.getCoach().getEmail(), reservation.getCoach().getPhone()) : null;
        
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
