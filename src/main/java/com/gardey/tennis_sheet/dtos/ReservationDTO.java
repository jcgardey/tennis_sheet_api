package com.gardey.tennis_sheet.dtos;

import java.time.Instant;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gardey.tennis_sheet.models.ReservationType;

public record ReservationDTO(
    Long id,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
    Instant start,
    long durationMinutes,
    ReservationType type,
    String description,
    List<PersonDTO> players,
    PersonDTO coach,
    String colorCode
) {
}
