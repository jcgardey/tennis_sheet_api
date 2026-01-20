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
        inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    private List<Person> players;

    @ManyToOne
    @JoinColumn(name = "coach_id")
    private Person coach;

    protected Reservation() {}

    public Reservation(Court court, Instant start, Instant end, ReservationType type, String description, List<Person> players, Person coach, String colorCode) {
        this.court = court;
        this.start = start;
        this.end = end;
        this.type = type;
        this.description = description;
        this.players = players;
        this.coach = coach;
        this.colorCode = colorCode;
    }

    public Reservation(Long id, Court court, Instant start, Instant end, ReservationType type, String description, List<Person> players, Person coach, String colorCode) {
        this.id = id;
        this(court, start, end, type, description, players, coach, colorCode);
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

    public List<Person> getPlayers() {
        return players;
    }

    public Person getCoach() {
        return coach;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPlayers(List<Person> players) {
        this.players = players;
    }

    public void setCoach(Person coach) {
        this.coach = coach;
    }
}
