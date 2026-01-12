package com.gardey.tennis_sheet.controllers;

import com.gardey.tennis_sheet.dtos.CreateReservationResponseDTO;
import com.gardey.tennis_sheet.services.ReservationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/courts/{courtId}/reservations")
    public List<CreateReservationResponseDTO> getReservationsForCourtOnDate(@PathVariable Long courtId, @RequestParam String date) {
        LocalDate d = LocalDate.parse(date);
        return reservationService.getReservationsForCourtOnDate(courtId, d);
    }
}
