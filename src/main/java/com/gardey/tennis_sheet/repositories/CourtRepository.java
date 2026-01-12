package com.gardey.tennis_sheet.repositories;

import com.gardey.tennis_sheet.models.Court;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourtRepository extends JpaRepository<Court, Long> {
    boolean existsByName(String name);
}