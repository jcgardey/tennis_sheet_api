package com.gardey.tennis_sheet.models;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    @Column(name = "start_date_time", nullable = false)
    private Instant start;

    @Column(name = "end_date_time", nullable = false)
    private Instant end;

    @Column(name = "color_code", length = 100)
    private String colorCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ReservationType type;

    @Column(name = "description")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "reservation_players",
        joinColumns = @JoinColumn(name = "reservation_id"),
        inverseJoinColumns = @JoinColumn(name = "player_profile_id")
    )
    private List<PlayerProfile> players;

    @ManyToOne
    @JoinColumn(name = "coach_profile_id")
    private CoachProfile coachProfile;

    protected Reservation() {}

    public Reservation(Court court, Instant start, Instant end, ReservationType type, String description, List<PlayerProfile> players, CoachProfile coachProfile, String colorCode) {
        this.court = court;
        this.start = start;
        this.end = end;
        this.type = type;
        this.description = description;
        this.players = players;
        this.coachProfile = coachProfile;
        this.colorCode = colorCode;
    }

    public Reservation(Long id, Court court, Instant start, Instant end, ReservationType type, String description, List<PlayerProfile> players, CoachProfile coachProfile, String colorCode) {
        this.id = id;
        this(court, start, end, type, description, players, coachProfile, colorCode);
    }

    public Long getId() {
        return id;
    }

    public Court getCourt() {
        return court;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }

    public String getColorCode() {
        return colorCode;
    }

    public ReservationType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public List<PlayerProfile> getPlayers() {
        return players;
    }

    public CoachProfile getCoachProfile() {
        return coachProfile;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPlayers(List<PlayerProfile> players) {
        this.players = players;
    }

    public void setCoachProfile(CoachProfile coachProfile) {
        this.coachProfile = coachProfile;
    }
}
