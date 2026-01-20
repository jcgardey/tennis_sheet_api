package com.gardey.tennis_sheet.controllers;

import com.gardey.tennis_sheet.dtos.CreateReservationRequestDTO;
import com.gardey.tennis_sheet.dtos.ReservationDTO;
import com.gardey.tennis_sheet.services.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/courts/{courtId}/reservations")
    public ResponseEntity<ReservationDTO> createReservation(@PathVariable Long courtId, @RequestBody CreateReservationRequestDTO request) {
        ReservationDTO response = reservationService.createReservation(courtId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/courts/{courtId}/reservations")
    public ResponseEntity<List<ReservationDTO>> getReservationsForCourtOnDate(@PathVariable Long courtId, @RequestParam String date) {
        LocalDate d = LocalDate.parse(date);
        List<ReservationDTO> reservations = reservationService.getReservationsForCourtOnDate(courtId, d);
        return ResponseEntity.ok(reservations);
    }
}
