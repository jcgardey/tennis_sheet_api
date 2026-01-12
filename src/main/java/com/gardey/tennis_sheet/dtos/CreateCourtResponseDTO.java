package com.gardey.tennis_sheet.dtos;

public class CreateCourtResponseDTO {
    private final Long id;
    private final String name;

    public CreateCourtResponseDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}