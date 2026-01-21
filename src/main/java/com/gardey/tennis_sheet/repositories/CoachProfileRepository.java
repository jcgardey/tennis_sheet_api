package com.gardey.tennis_sheet.repositories;

import com.gardey.tennis_sheet.models.CoachProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoachProfileRepository extends JpaRepository<CoachProfile, Long> {
    Optional<CoachProfile> findByPersonId(Long personId);
}
