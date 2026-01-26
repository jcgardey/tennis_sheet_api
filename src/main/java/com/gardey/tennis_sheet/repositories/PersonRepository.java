package com.gardey.tennis_sheet.repositories;

import com.gardey.tennis_sheet.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    
    Optional<Person> findByEmailIgnoreCase(String email);

    @Query("SELECT p FROM Person p WHERE p.playerProfile IS NOT NULL AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Person> findAllPlayersByName(String name);
    
    @Query("SELECT p FROM Person p WHERE p.coachProfile IS NOT NULL AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Person> findAllCoachesByName(String name);
}