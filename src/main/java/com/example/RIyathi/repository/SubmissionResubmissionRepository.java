package com.example.RIyathi.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.RIyathi.entity.SubmissionResubmission;

public interface SubmissionResubmissionRepository extends JpaRepository<SubmissionResubmission, Long> {

	List<SubmissionResubmission> findByClaimTableId(Long claimTableId);

	List<SubmissionResubmission> findByClaimTableIdIn(Collection<Long> claimTableIds);
}
