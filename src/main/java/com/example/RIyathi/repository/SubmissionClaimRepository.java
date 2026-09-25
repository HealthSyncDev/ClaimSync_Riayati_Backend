package com.example.RIyathi.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.RIyathi.entity.SubmissionClaim;

public interface SubmissionClaimRepository extends JpaRepository<SubmissionClaim, Long> {

	List<SubmissionClaim> findByHeaderTableId(Long headerTableId);

	List<SubmissionClaim> findByHeaderTableIdIn(Collection<Long> headerTableIds);
}
