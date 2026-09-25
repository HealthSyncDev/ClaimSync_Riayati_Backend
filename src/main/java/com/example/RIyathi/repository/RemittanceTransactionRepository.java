package com.example.RIyathi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.RIyathi.entity.RemittanceTransaction;

public interface RemittanceTransactionRepository extends JpaRepository<RemittanceTransaction, Long> {

	@Query("SELECT t FROM RemittanceTransaction t WHERE t.id = :id")
	Optional<RemittanceTransaction> findFirstById(@Param("id") String id);

	@Query("SELECT t FROM RemittanceTransaction t WHERE t.id = :id AND t.officeId = :officeId")
	Optional<RemittanceTransaction> findFirstByIdAndOfficeId(@Param("id") String id, @Param("officeId") Long officeId);

	List<RemittanceTransaction> findByIsExtracted(Integer isExtracted);

	List<RemittanceTransaction> findByIsExtractedAndOfficeId(Integer isExtracted, Long officeId);
}
