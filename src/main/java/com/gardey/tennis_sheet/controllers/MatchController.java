package com.gardey.tennis_sheet.controllers;

import com.gardey.tennis_sheet.dtos.CreateMatchRequestDTO;
import com.gardey.tennis_sheet.dtos.CreateMatchResponseDTO;
import com.gardey.tennis_sheet.exceptions.ReservationConflictException;
import com.gardey.tennis_sheet.services.MatchService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courts/{courtId}/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateMatchResponseDTO create(@PathVariable Long courtId, @RequestBody CreateMatchRequestDTO request) throws ReservationConflictException {
        return matchService.createMatch(courtId, request);
    }
}
