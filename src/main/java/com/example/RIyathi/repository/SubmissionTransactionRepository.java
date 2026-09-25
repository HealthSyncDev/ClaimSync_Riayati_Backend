package com.example.RIyathi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.RIyathi.entity.SubmissionTransaction;

public interface SubmissionTransactionRepository extends JpaRepository<SubmissionTransaction, Long> {

	@Query("SELECT t FROM SubmissionTransaction t WHERE t.id = :id")
	Optional<SubmissionTransaction> findFirstById(@Param("id") String id);

	@Query("SELECT t FROM SubmissionTransaction t WHERE t.id = :id AND t.officeId = :officeId")
	Optional<SubmissionTransaction> findFirstByIdAndOfficeId(@Param("id") String id, @Param("officeId") Long officeId);

	List<SubmissionTransaction> findByIsExtracted(Integer isExtracted);

	List<SubmissionTransaction> findByIsExtractedAndOfficeId(Integer isExtracted, Long officeId);
}
