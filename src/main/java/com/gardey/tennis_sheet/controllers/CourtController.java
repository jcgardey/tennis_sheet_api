package com.gardey.tennis_sheet.controllers;

import com.gardey.tennis_sheet.dtos.CreateCourtRequestDTO;
import com.gardey.tennis_sheet.dtos.CreateCourtResponseDTO;
import com.gardey.tennis_sheet.exceptions.CourtNameAlreadyExistsException;
import com.gardey.tennis_sheet.services.CourtService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/courts")
public class CourtController {

    private final CourtService courtService;

    public CourtController(CourtService courtService) {
        this.courtService = courtService;
    }

    @GetMapping
    public List<CreateCourtResponseDTO> list() {
        return courtService.getAllCourts();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCourtResponseDTO create(@RequestBody CreateCourtRequestDTO request) throws CourtNameAlreadyExistsException {
        return courtService.createCourt(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        courtService.deleteCourt(id);
    }

}