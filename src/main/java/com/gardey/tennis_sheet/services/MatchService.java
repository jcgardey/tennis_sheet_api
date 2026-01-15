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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class MatchService {

    private final ReservationRepository reservationRepository;
    private final CourtRepository courtRepository;
    private final AppConfigurationRepository configurationRepository;

    public MatchService(ReservationRepository reservationRepository, CourtRepository courtRepository, AppConfigurationRepository configurationRepository) {
        this.reservationRepository = reservationRepository;
        this.courtRepository = courtRepository;
        this.configurationRepository = configurationRepository;
    }

    @Transactional
    public CreateMatchResponseDTO createMatch(Long courtId, CreateMatchRequestDTO request) throws ReservationConflictException {
        long duration = request.getDurationMinutes();
        if (duration <= 0) {
            throw new RuntimeException("Duration must be positive minutes");
        }

        Instant end = request.getStart().plus(duration, ChronoUnit.MINUTES);
        if (!courtRepository.existsById(courtId)) {
            throw new ResourceNotFoundException("Court not found with id: " + courtId);
        }

        if (reservationRepository.existsByCourtIdAndTimeRangeOverlap(courtId, request.getStart(), end)) {
            throw new ReservationConflictException(courtId, request.getStart().toString());
        }

        Court court = courtRepository.findById(courtId).get();
        String colorCode = configurationRepository.findValueByKey(AppConfiguration.ConfigurationKey.MATCH_DEFAULT_COLOR_CODE).orElse(null);
        Reservation reservation = new Match(court, request.getStart(), end, request.getPlayerName(), request.getContactPhone(), colorCode);
        Reservation saved = reservationRepository.save(reservation);

        return new CreateMatchResponseDTO(saved.getId(), saved.getStart(), duration, request.getPlayerName(), request.getContactPhone(), colorCode);
    }
}
