package com.gardey.tennis_sheet.services;

import com.gardey.tennis_sheet.dtos.CreateCourtRequestDTO;
import com.gardey.tennis_sheet.dtos.CreateCourtResponseDTO;
import com.gardey.tennis_sheet.exceptions.CourtNameAlreadyExistsException;
import com.gardey.tennis_sheet.exceptions.ResourceNotFoundException;
import com.gardey.tennis_sheet.models.Court;
import com.gardey.tennis_sheet.repositories.CourtRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourtService {

    private final CourtRepository courtRepository;

    public CourtService(CourtRepository courtRepository) {
        this.courtRepository = courtRepository;
    }

    @Transactional(readOnly = true)
    public List<CreateCourtResponseDTO> getAllCourts() {
        return courtRepository.findAll().stream()
                .map(court -> new CreateCourtResponseDTO(court.getId(), court.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public CreateCourtResponseDTO createCourt(CreateCourtRequestDTO request) throws CourtNameAlreadyExistsException {
        if (courtRepository.existsByName(request.name())) {
            throw new CourtNameAlreadyExistsException(request.name());
        }

        Court court = new Court(request.name());
        Court savedCourt = courtRepository.save(court);

        return new CreateCourtResponseDTO(savedCourt.getId(), savedCourt.getName());
    }

    @Transactional
    public void deleteCourt(Long id) {
        if (!courtRepository.existsById(id)) {
            throw new ResourceNotFoundException("Court not found with id: " + id);
        }
        courtRepository.deleteById(id);
    }
}