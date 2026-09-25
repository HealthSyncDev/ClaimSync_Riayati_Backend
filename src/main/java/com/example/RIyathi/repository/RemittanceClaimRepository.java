package com.example.RIyathi.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.RIyathi.entity.RemittanceClaim;

public interface RemittanceClaimRepository extends JpaRepository<RemittanceClaim, Long> {

	List<RemittanceClaim> findByClaimId(String claimId);

	List<RemittanceClaim> findByHeaderTableIdIn(Collection<Long> headerTableIds);
}
