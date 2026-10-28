package com.example.RIyathi.repository;

import com.example.RIyathi.entity.CptCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CptCodeRepository extends JpaRepository<CptCode, Long> {
}
