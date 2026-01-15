package com.gardey.tennis_sheet.repositories;

import com.gardey.tennis_sheet.models.AppConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppConfigurationRepository extends JpaRepository<AppConfiguration, Long> {
    
    Optional<AppConfiguration> findByKey(AppConfiguration.ConfigurationKey key);
    
    @Query("SELECT c.value FROM AppConfiguration c WHERE c.key = :key")
    Optional<String> findValueByKey(@Param("key") AppConfiguration.ConfigurationKey key);
    
    boolean existsByKey(AppConfiguration.ConfigurationKey key);
}