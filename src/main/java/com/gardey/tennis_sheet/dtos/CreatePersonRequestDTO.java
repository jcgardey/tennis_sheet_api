package com.gardey.tennis_sheet.dtos;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreatePersonRequestDTO {
    private final String name;
    private final String email;
    private final String phone;

    @JsonProperty(value="isCoach")  
    private final boolean isCoach;

    private final String colorCode;
    private final BigDecimal hourlyRate;

    public CreatePersonRequestDTO(String name, String email, String phone, boolean isCoach, String colorCode, BigDecimal hourlyRate) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.isCoach = isCoach;
        this.colorCode = colorCode;
        this.hourlyRate = hourlyRate;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isCoach() {
        return isCoach;
    }

    public String getColorCode() {
        return colorCode;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }
}