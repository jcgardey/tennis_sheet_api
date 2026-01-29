package com.gardey.tennis_sheet.dtos;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CreatePersonRequestDTO(
    String name,
    String email,
    String phone,
    @JsonProperty(value="isCoach")
    boolean isCoach,
    String colorCode,
    BigDecimal hourlyRate
) {
}