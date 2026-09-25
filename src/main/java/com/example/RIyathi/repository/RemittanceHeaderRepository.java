package com.example.RIyathi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.RIyathi.entity.RemittanceHeader;

public interface RemittanceHeaderRepository extends JpaRepository<RemittanceHeader, Long> {

	List<RemittanceHeader> findByOfficeId(Long officeId);
}
