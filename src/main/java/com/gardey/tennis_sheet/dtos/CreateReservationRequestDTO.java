package com.gardey.tennis_sheet.dtos;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

public class CreateReservationRequestDTO {
    private final Long courtId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private final Instant start;
    
    private final long durationMinutes;

    public CreateReservationRequestDTO(Long courtId, Instant start, long durationMinutes) {
        this.courtId = courtId;
        this.start = start;
        this.durationMinutes = durationMinutes;
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

}
