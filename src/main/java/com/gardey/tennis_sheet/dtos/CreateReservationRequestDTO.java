package com.gardey.tennis_sheet.dtos;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gardey.tennis_sheet.models.ReservationType;

public class CreateReservationRequestDTO {
    private final Long courtId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private final Instant start;
    
    private final long durationMinutes;
    
    private final ReservationType type;
    
    private final String description;
    
    private final List<Long> playerIds;
    
    private final Long coachId;

    public CreateReservationRequestDTO(Long courtId, Instant start, long durationMinutes, ReservationType type, String description, List<Long> playerIds, Long coachId) {
        this.courtId = courtId;
        this.start = start;
        this.durationMinutes = durationMinutes;
        this.type = type;
        this.description = description;
        this.playerIds = playerIds;
        this.coachId = coachId;
    }

    public Long getCourtId() {
        return courtId;
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

    public List<Long> getPlayerIds() {
        return playerIds;
    }

    public Long getCoachId() {
        return coachId;
    }
}
