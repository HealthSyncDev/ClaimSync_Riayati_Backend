package com.example.RIyathi.repository;

import com.example.RIyathi.entity.Clinician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClinicianRepository extends JpaRepository<Clinician, Long> {
}
