package com.gardey.tennis_sheet.dtos;

import java.time.Instant;

public class CreateMatchResponseDTO  {
    
    private final Long id;
    private final Instant start;
    private final long durationMinutes;
    private final String playerName;
    private final String contactPhone;
    private final String colorCode;

    public CreateMatchResponseDTO(Long id, Instant start, long durationMinutes, String playerName, String contactPhone, String colorCode) {
        this.id = id;
        this.start = start;
        this.durationMinutes = durationMinutes;
        this.playerName = playerName;
        this.contactPhone = contactPhone;
        this.colorCode = colorCode;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getContactPhone() {
        return contactPhone;
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

    public String getColorCode() {
        return colorCode;
    }
}
