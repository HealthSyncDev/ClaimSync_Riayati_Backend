package com.example.RIyathi.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class ClaimReportService {

	@PersistenceContext
	private EntityManager entityManager;

	public Object list(ReportQueryParams query, int page, int size) {
		int pageNumber = page < 1 ? 1 : page;
		int pageSize = size < 1 || size > 500 ? 500 : size;
		Loaded loaded = load(query, pageNumber, pageSize, true);

		List<Map<String, Object>> data = new ArrayList<>(loaded.rows());
		data.add(loaded.total());

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("page", pageNumber);
		body.put("size", pageSize);
		body.put("totalElements", loaded.totalElements());
		body.put("totalPages", loaded.totalPages());
		body.put("officeId", query.getOfficeId());
		body.put("officeIds", loaded.officeIds());
		body.put("data", data);
		return body;
	}

	public List<Map<String, Object>> exportRows(ReportQueryParams query) {
		Loaded loaded = load(query, 1, Integer.MAX_VALUE, false);
		List<Map<String, Object>> rows = new ArrayList<>(loaded.rows());
		rows.add(loaded.total());
		return rows;
	}

	private Loaded load(ReportQueryParams query, int pageNumber, int pageSize, boolean paginate) {
		ActivityReportQueryBuilder.Filter filter = ActivityReportQueryBuilder.build(
				query.getOfficeId(), query.getOfficeIds(), query.getSearchOn(), query.getFromDate(), query.getToDate(),
				query.getYear(), query.getMonth(), query.toFilters(), true);
		String from = " FROM tbl_activityReport ar" + filter.whereSql();

		Number totalInvoices = (Number) bind(entityManager.createNativeQuery(
				"SELECT COUNT(DISTINCT ar.InvoiceNo)" + from), filter.params()).getSingleResult();
		long totalElements = totalInvoices == null ? 0L : totalInvoices.longValue();
		int totalPages = !paginate || totalElements == 0
				? (totalElements == 0 ? 0 : 1)
				: (int) Math.ceil(totalElements / (double) pageSize);

		Query pageQuery = bind(entityManager.createNativeQuery(selectSql(from)), filter.params());
		if (paginate) {
			pageQuery.setFirstResult((pageNumber - 1) * pageSize);
			pageQuery.setMaxResults(pageSize);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> rows = pageQuery.getResultList();
		List<Map<String, Object>> data = new ArrayList<>();
		for (Object[] row : rows) {
			data.add(toInvoiceRow(row));
		}
		return new Loaded(data, toTotalRow(loadTotals(from, filter.params())), totalElements, totalPages, filter.officeIds());
	}

	private String selectSql(String from) {
		return """
				SELECT
				  MAX(ar.officeid) AS facilityGroupId,
				  CAST(NULL AS CHAR) AS healthAuthority,
				  MAX(ar.FacilityID) AS facilityId,
				  MAX(ar.Facility_Name) AS facilityName,
				  ar.InvoiceNo AS claimNumber,
				  MAX(ar.ReceiverID) AS receiverId,
				  MAX(ar.Receiver_Name) AS receiverName,
				  MAX(ar.PayerID) AS payerId,
				  MAX(ar.Payer_Name) AS payerName,
				  MAX(ar.PatientID) AS patientId,
				  MAX(ar.EncounterStartDate) AS encounterStartDate,
				  MAX(ar.EncounterEndDate) AS encounterEndDate,
				  MAX(ar.EncounterType) AS encounterType,
				  CAST(NULL AS CHAR) AS startType,
				  CAST(NULL AS CHAR) AS endType,
				  MAX(ar.MemberID) AS memberId,
				  MAX(ar.NationalIDNumber) AS emiratesIdNumber,
				  MAX(ar.IDPayer) AS idPayer,
				  MAX(ar.TransactionDate) AS submissionDate,
				  MAX(ar.OrderingClinician) AS orderingClinician,
				  MAX(ar.OrderingClinician_Name) AS orderingClinicianName,
				  MAX(ar.Clinician) AS clinicianCode,
				  MAX(ar.Clinician_Name) AS clinicianName,
				  SUM(ar.GrossAmt) AS grossAmt,
				  SUM(ar.NetAmt) AS netAmt,
				  SUM(ar.InitialNetAmt) AS initialNetAmt,
				  SUM(ar.PatientShare) AS claimPatientShareAmt,
				  SUM(ar.RemittedAmt) AS paymentAmt,
				  SUM(ar.RejectedAmt) AS rejectedAmt,
				  SUM(ar.InitialRejectedAmt) AS initialRejectedAmt,
				  SUM(ar.WriteOffAmt) AS writeOffAmt,
				  CAST(NULL AS CHAR) AS remittanceStatus,
				  MAX(ar.ClaimStatus) AS claimStatus,
				  MAX(ar.PaymentReference) AS paymentReference,
				  MAX(ar.ResubmissionType) AS resubmissionType,
				  MAX(ar.LastRemittanceDate) AS lastRemittanceDate,
				  COALESCE(MAX(ar.LastResubmissionDate), MAX(ar.TransactionDate)) AS lastSubmissionDate,
				  MAX(ar.LastSubmissionTransactionId) AS lastSubmissionTransactionId,
				  MAX(ar.SubmissionAllTransactionIds) AS submissionAllTransactionIds,
				  MAX(ar.LastRemittanceTransactionId) AS lastRemittanceTransactionId,
				  CAST(NULL AS CHAR) AS allRemittanceFiles,
				  CAST(NULL AS CHAR) AS remittanceAllTransactionIds,
				  MAX(ar.RemittanceComment) AS remittanceComment,
				  MAX(ar.ResubmissionComment) AS resubmissionComment,
				  MAX(ar.ResubmissionCount) AS resubmissionCount,
				  SUM(ar.SettledAmt) AS settledAmt,
				  MAX(ar.ReceiptStatus) AS receiptStatus,
				  MAX(ar.InitialDateSettlement) AS initialDateSettlement,
				  MAX(ar.PaymentStatus) AS paymentStatus,
				  MAX(ar.patient_type) AS patientType,
				  MAX(ar.encounter_type_description) AS encounterTypeDescription,
				  MAX(ar.officeid) AS officeId
				""" + from + """
				 GROUP BY ar.InvoiceNo
				 ORDER BY ar.InvoiceNo
				""";
	}

	private Object[] loadTotals(String from, Map<String, Object> params) {
		String sql = """
				SELECT
				  SUM(ar.GrossAmt),
				  SUM(ar.NetAmt),
				  SUM(ar.InitialNetAmt),
				  SUM(ar.PatientShare),
				  SUM(ar.RemittedAmt),
				  SUM(ar.RejectedAmt),
				  SUM(ar.InitialRejectedAmt),
				  SUM(ar.WriteOffAmt),
				  SUM(ar.SettledAmt)
				""" + from;
		Object result = bind(entityManager.createNativeQuery(sql), params).getSingleResult();
		if (result instanceof Object[] array) {
			return array;
		}
		return new Object[] { result, null, null, null, null, null, null, null, null };
	}

	private Map<String, Object> toInvoiceRow(Object[] row) {
		Map<String, Object> item = new LinkedHashMap<>();
		item.put("isTotalRow", false);
		item.put("facilityGroupId", row[0]);
		item.put("healthAuthority", row[1]);
		item.put("facilityId", row[2]);
		item.put("facilityName", row[3]);
		item.put("claimNumber", row[4]);
		item.put("receiverId", row[5]);
		item.put("receiverName", row[6]);
		item.put("payerId", row[7]);
		item.put("payerName", row[8]);
		item.put("patientId", row[9]);
		item.put("encounterStartDate", row[10]);
		item.put("encounterEndDate", row[11]);
		item.put("encounterType", row[12]);
		item.put("startType", row[13]);
		item.put("endType", row[14]);
		item.put("memberId", row[15]);
		item.put("emiratesIdNumber", row[16]);
		item.put("idPayer", row[17]);
		item.put("submissionDate", row[18]);
		item.put("orderingClinician", row[19]);
		item.put("orderingClinicianName", row[20]);
		item.put("clinicianCode", row[21]);
		item.put("clinicianName", row[22]);
		item.put("grossAmt", row[23]);
		item.put("netAmt", row[24]);
		item.put("initialNetAmt", row[25]);
		item.put("claimPatientShareAmt", row[26]);
		item.put("paymentAmt", row[27]);
		item.put("rejectedAmt", row[28]);
		item.put("initialRejectedAmt", row[29]);
		item.put("writeOffAmt", row[30]);
		item.put("remittanceStatus", row[31]);
		item.put("claimStatus", row[32]);
		item.put("paymentReference", row[33]);
		item.put("resubmissionType", row[34]);
		item.put("lastRemittanceDate", row[35]);
		item.put("lastSubmissionDate", row[36]);
		item.put("lastSubmissionTransactionId", row[37]);
		item.put("submissionAllTransactionIds", row[38]);
		item.put("lastRemittanceTransactionId", row[39]);
		item.put("allRemittanceFiles", row[40]);
		item.put("remittanceAllTransactionIds", row[41]);
		item.put("remittanceComment", row[42]);
		item.put("resubmissionComment", row[43]);
		item.put("resubmissionCount", row[44]);
		item.put("settledAmt", row[45]);
		item.put("receiptStatus", row[46]);
		item.put("initialDateSettlement", row[47]);
		item.put("paymentStatus", row[48]);
		item.put("patientType", row[49]);
		item.put("encounterTypeDescription", row[50]);
		item.put("officeId", row[51]);
		return item;
	}

	private Map<String, Object> toTotalRow(Object[] totals) {
		Map<String, Object> total = new LinkedHashMap<>();
		total.put("isTotalRow", true);
		total.put("label", "TOTAL");
		total.put("grossAmt", amount(totals, 0));
		total.put("netAmt", amount(totals, 1));
		total.put("initialNetAmt", amount(totals, 2));
		total.put("claimPatientShareAmt", amount(totals, 3));
		total.put("paymentAmt", amount(totals, 4));
		total.put("rejectedAmt", amount(totals, 5));
		total.put("initialRejectedAmt", amount(totals, 6));
		total.put("writeOffAmt", amount(totals, 7));
		total.put("settledAmt", amount(totals, 8));
		return total;
	}

	private Object amount(Object[] totals, int index) {
		if (totals == null || index >= totals.length) {
			return BigDecimal.ZERO;
		}
		return totals[index] == null ? BigDecimal.ZERO : totals[index];
	}

	private Query bind(Query query, Map<String, Object> params) {
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return query;
	}

	private record Loaded(
			List<Map<String, Object>> rows,
			Map<String, Object> total,
			long totalElements,
			int totalPages,
			List<Long> officeIds) {
	}
}
