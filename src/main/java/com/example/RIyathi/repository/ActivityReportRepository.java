package com.example.RIyathi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.RIyathi.entity.ActivityReport;

public interface ActivityReportRepository extends JpaRepository<ActivityReport, Long> {

	@Transactional
	void deleteByOfficeId(Long officeId);

	Page<ActivityReport> findByOfficeId(Long officeId, Pageable pageable);

	@Query("""
			select coalesce(sum(a.grossAmt), 0),
			       coalesce(sum(a.netAmt), 0),
			       coalesce(sum(a.initialNetAmt), 0),
			       coalesce(sum(a.patientShare), 0),
			       coalesce(sum(a.remittedAmt), 0),
			       coalesce(sum(a.rejectedAmt), 0),
			       coalesce(sum(a.initialRejectedAmt), 0),
			       coalesce(sum(a.writeOffAmt), 0),
			       coalesce(sum(a.settledAmt), 0)
			from ActivityReport a
			""")
	List<Object[]> sumAmounts();

	@Query("""
			select coalesce(sum(a.grossAmt), 0),
			       coalesce(sum(a.netAmt), 0),
			       coalesce(sum(a.initialNetAmt), 0),
			       coalesce(sum(a.patientShare), 0),
			       coalesce(sum(a.remittedAmt), 0),
			       coalesce(sum(a.rejectedAmt), 0),
			       coalesce(sum(a.initialRejectedAmt), 0),
			       coalesce(sum(a.writeOffAmt), 0),
			       coalesce(sum(a.settledAmt), 0)
			from ActivityReport a
			where a.officeId = :officeId
			""")
	List<Object[]> sumAmountsByOfficeId(@Param("officeId") Long officeId);
}
