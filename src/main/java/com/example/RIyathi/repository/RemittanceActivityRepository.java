package com.example.RIyathi.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.RIyathi.entity.RemittanceActivity;

public interface RemittanceActivityRepository extends JpaRepository<RemittanceActivity, Long> {

	List<RemittanceActivity> findByClaimTableId(Long claimTableId);

	List<RemittanceActivity> findByClaimTableIdIn(Collection<Long> claimTableIds);
}
