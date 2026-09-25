package com.example.RIyathi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.RIyathi.entity.Office;

public interface OfficeRepository extends JpaRepository<Office, Long> {

	Optional<Office> findFirstByUsername(String username);

	Optional<Office> findFirstByFacilityId(String facilityId);

	List<Office> findByStatus(Integer status);
}
