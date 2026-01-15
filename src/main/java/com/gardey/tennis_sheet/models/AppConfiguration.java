package com.gardey.tennis_sheet.models;

import jakarta.persistence.*;

@Entity
@Table(name = "app_configurations")
public class AppConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "config_key", unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private ConfigurationKey key;

    @Column(name = "config_value", nullable = false, columnDefinition = "TEXT")
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(name = "value_type", nullable = false)
    private ConfigurationType type;

    @Column(name = "description", length = 500)
    private String description;

    
    public AppConfiguration() {}

    // Constructor for creating new configurations
    public AppConfiguration(ConfigurationKey key, String value, ConfigurationType type, String description) {
        this.key = key;
        this.value = value;
        this.type = type;
        this.description = description;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public ConfigurationKey getKey() {
        return key;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ConfigurationType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public enum ConfigurationType {
        STRING, NUMBER, BOOLEAN
    }

    public enum ConfigurationKey {
        MATCH_DEFAULT_COLOR_CODE
    }
}