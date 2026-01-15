package com.gardey.tennis_sheet.models;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "reservations")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Reservation {

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

    protected Reservation() {}

    public Reservation(Court court, Instant start, Instant end) {
        this.court = court;
        this.start = start;
        this.end = end;
    }

    public Reservation(Court court, Instant start, Instant end, String colorCode) {
        this.court = court;
        this.start = start;
        this.end = end;
        this.colorCode = colorCode;
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

    public abstract String getDescription();
}
