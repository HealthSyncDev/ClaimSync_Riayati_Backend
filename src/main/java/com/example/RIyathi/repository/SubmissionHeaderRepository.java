package com.example.RIyathi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.RIyathi.entity.SubmissionHeader;

public interface SubmissionHeaderRepository extends JpaRepository<SubmissionHeader, Long> {

	List<SubmissionHeader> findByOfficeId(Long officeId);
}
