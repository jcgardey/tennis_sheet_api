package com.gardey.tennis_sheet.dtos;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gardey.tennis_sheet.models.ReservationType;

public record CreateReservationRequestDTO(
    Long courtId,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    Instant start,
    long durationMinutes,
    ReservationType type,
    String description,
    List<Long> playerIds,
    Long coachId
) {
}
