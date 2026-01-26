package com.gardey.tennis_sheet.models;

import jakarta.persistence.*;

@Entity
@Table(name = "persons")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @OneToOne(mappedBy = "person", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private PlayerProfile playerProfile;
    
    @OneToOne(mappedBy = "person", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private CoachProfile coachProfile;

    protected Person() {}

     public Person(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public Person(Long id, String name, String email, String phone) {
        this.id = id;
        this(name, email, phone);
    }

    public Long getId() {
        return id;
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

    public PlayerProfile getPlayerProfile() {
        return playerProfile;
    }

    public void setPlayerProfile(PlayerProfile playerProfile) {
        this.playerProfile = playerProfile;
    }

    public void setCoachProfile(CoachProfile coachProfile) {
        this.coachProfile = coachProfile;
    }
}