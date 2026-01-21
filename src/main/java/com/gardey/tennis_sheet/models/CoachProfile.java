package com.gardey.tennis_sheet.models;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "coach_profiles")
public class CoachProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false, unique = true)
    private Person person;

    @Column(name = "color_code", length = 7)
    private String colorCode;

    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    protected CoachProfile() {}

    public CoachProfile(Person person) {
        this.person = person;
    }

    public CoachProfile(Person person, String colorCode, BigDecimal hourlyRate) {
        this.person = person;
        this.colorCode = colorCode;
        this.hourlyRate = hourlyRate;
    }

    public Long getId() {
        return id;
    }

    public Person getPerson() {
        return person;
    }

    public String getColorCode() {
        return colorCode;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
}
