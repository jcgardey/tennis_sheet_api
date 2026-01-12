package com.gardey.tennis_sheet.services;

import com.gardey.tennis_sheet.dtos.CreateReservationResponseDTO;
import com.gardey.tennis_sheet.exceptions.ResourceNotFoundException;
import com.gardey.tennis_sheet.models.Reservation;
import com.gardey.tennis_sheet.repositories.CourtRepository;
import com.gardey.tennis_sheet.repositories.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CourtRepository courtRepository;

    public ReservationService(ReservationRepository reservationRepository, CourtRepository courtRepository) {
        this.reservationRepository = reservationRepository;
        this.courtRepository = courtRepository;
    }

    @Transactional(readOnly = true)
    public List<CreateReservationResponseDTO> getReservationsForCourtOnDate(Long courtId, LocalDate date) {
        if (!courtRepository.existsById(courtId)) {
            throw new ResourceNotFoundException("Court not found with id: " + courtId);
        }

        Instant startOfDay = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfDay = date.atTime(LocalTime.MAX).plusNanos(1).atZone(ZoneOffset.UTC).toInstant();
        

    

        List<Reservation> reservations = reservationRepository.findByCourtIdAndStartBetween(courtId, startOfDay, endOfDay);

        return reservations.stream()
            .map(r -> new CreateReservationResponseDTO(r.getId(), r.getStart(), Duration.between(r.getStart(), r.getEnd()).toMinutes(), r.getDescription()))
            .collect(Collectors.toList());
    }
}
