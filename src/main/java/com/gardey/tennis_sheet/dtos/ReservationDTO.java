package com.gardey.tennis_sheet.dtos;

import java.time.Instant;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gardey.tennis_sheet.models.ReservationType;

public class ReservationDTO {
    private final Long id;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
    private final Instant start;

    private final long durationMinutes;

    private final ReservationType type;
    
    private final String description;
    
    private final List<PersonDTO> players;
    
    private final PersonDTO coach;
    
    private final String colorCode;

    public ReservationDTO(Long id, Instant start, long durationMinutes, ReservationType type, String description, List<PersonDTO> players, PersonDTO coach, String colorCode) {
        this.id = id;
        this.start = start;
        this.durationMinutes = durationMinutes;
        this.type = type;
        this.description = description;
        this.players = players;
        this.coach = coach;
        this.colorCode = colorCode;
    }

    public Long getId() {
        return id;
    }

    public Instant getStart() {
        return start;
    }

    public long getDurationMinutes() {
        return durationMinutes;
    }

    public ReservationType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public List<PersonDTO> getPlayers() {
        return players;
    }

    public PersonDTO getCoach() {
        return coach;
    }

    public String getColorCode() {
        return colorCode;
    }
}
