package com.gardey.tennis_sheet.repositories;

import com.gardey.tennis_sheet.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findByNameContainingIgnoreCase(String name);
    List<Person> findByEmailIgnoreCase(String email);
}