package com.gardey.tennis_sheet.models;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "matches")
@PrimaryKeyJoinColumn(name = "id")
public class Match extends Reservation {

    @Column(name = "player_name")
    private String playerName;

    @Column(name = "contact_phone")
    private String contactPhone;

    protected Match() {}

    public Match(Court court, Instant start, Instant end, String playerName, String contactPhone) {
        super(court, start, end);
        this.playerName = playerName;
        this.contactPhone = contactPhone;
    }

    public Match(Court court, Instant start, Instant end, String playerName, String contactPhone, String colorCode) {
        super(court, start, end, colorCode);
        this.playerName = playerName;
        this.contactPhone = contactPhone;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    @Override
    public String getDescription() {
        return playerName;
    }
}
