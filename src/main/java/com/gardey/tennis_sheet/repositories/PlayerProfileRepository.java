package com.gardey.tennis_sheet.repositories;

import com.gardey.tennis_sheet.models.PlayerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerProfileRepository extends JpaRepository<PlayerProfile, Long> {
    Optional<PlayerProfile> findByPersonId(Long personId);
}
