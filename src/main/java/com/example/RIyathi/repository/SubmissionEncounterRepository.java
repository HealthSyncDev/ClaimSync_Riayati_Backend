package com.example.RIyathi.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.RIyathi.entity.SubmissionEncounter;

public interface SubmissionEncounterRepository extends JpaRepository<SubmissionEncounter, Long> {

	Optional<SubmissionEncounter> findFirstByClaimTableId(Long claimTableId);

	List<SubmissionEncounter> findByClaimTableId(Long claimTableId);

	List<SubmissionEncounter> findByClaimTableIdIn(Collection<Long> claimTableIds);
}
