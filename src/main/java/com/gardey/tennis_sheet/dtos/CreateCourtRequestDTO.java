package com.gardey.tennis_sheet.dtos;

public class CreateCourtRequestDTO {
    private final String name;

    public CreateCourtRequestDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
