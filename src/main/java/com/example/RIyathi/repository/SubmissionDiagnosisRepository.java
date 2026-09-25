package com.example.RIyathi.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.RIyathi.entity.SubmissionDiagnosis;

public interface SubmissionDiagnosisRepository extends JpaRepository<SubmissionDiagnosis, Long> {

	List<SubmissionDiagnosis> findByClaimTableId(Long claimTableId);

	List<SubmissionDiagnosis> findByClaimTableIdIn(Collection<Long> claimTableIds);
}
