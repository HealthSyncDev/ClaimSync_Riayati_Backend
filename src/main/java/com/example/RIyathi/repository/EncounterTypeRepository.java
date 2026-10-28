package com.example.RIyathi.repository;

import com.example.RIyathi.entity.EncounterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncounterTypeRepository extends JpaRepository<EncounterType, Long> {
}
