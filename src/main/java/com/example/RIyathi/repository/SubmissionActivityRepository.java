package com.example.RIyathi.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.RIyathi.entity.SubmissionActivity;

public interface SubmissionActivityRepository extends JpaRepository<SubmissionActivity, Long> {

	List<SubmissionActivity> findByClaimTableId(Long claimTableId);

	List<SubmissionActivity> findByClaimTableIdIn(Collection<Long> claimTableIds);
}
