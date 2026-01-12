package com.gardey.tennis_sheet.dtos;

import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonFormat;

public class CreateReservationResponseDTO {
    private final Long id;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
    private final Instant start;

    private final long durationMinutes;

    private final String description;

    public CreateReservationResponseDTO(Long id, Instant start, long durationMinutes, String description) {
        this.id = id;
        this.start = start;
        this.durationMinutes = durationMinutes;
        this.description = description;
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

    public String getDescription() {
        return description;
    }
}
