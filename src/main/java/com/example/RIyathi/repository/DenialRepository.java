package com.example.RIyathi.repository;

import com.example.RIyathi.entity.Denial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DenialRepository extends JpaRepository<Denial, Long> {
}
