package com.gardey.tennis_sheet.models;

import jakarta.persistence.*;

@Entity
@Table(name = "courts")
public class Court {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
    
    protected Court() {}

    public Court(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}