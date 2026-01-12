package com.gardey.tennis_sheet.dtos;

import java.time.Instant;

public class CreateMatchRequestDTO extends CreateReservationRequestDTO {
    private final String playerName;
    private final String contactPhone;

    public CreateMatchRequestDTO(Long courtId, Instant start, int durationMinutes, String playerName, String contactPhone) {
        super(courtId, start, durationMinutes);
        this.playerName = playerName;
        this.contactPhone = contactPhone;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

}
