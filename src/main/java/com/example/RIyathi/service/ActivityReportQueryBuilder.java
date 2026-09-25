package com.example.RIyathi.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

final class ActivityReportQueryBuilder {

	private static final Set<String> DATE_COLUMNS = Set.of(
			"TransactionDate",
			"SubmissionTransactionDate",
			"ActivityStartDate",
			"EncounterStartDate",
			"EncounterEndDate",
			"LastRemittanceDate",
			"LastResubmissionDate",
			"FirstRemittanceDate",
			"InitialDateSettlement");

	private static final Map<String, String> SEARCH_ON_COLUMNS = Map.ofEntries(
			Map.entry("transactiondate", "TransactionDate"),
			Map.entry("submissiontransactiondate", "TransactionDate"),
			Map.entry("activitystartdate", "ActivityStartDate"),
			Map.entry("encounterstartdate", "EncounterStartDate"),
			Map.entry("encounterenddate", "EncounterEndDate"),
			Map.entry("lastremittancedate", "LastRemittanceDate"),
			Map.entry("lastresubmissiondate", "LastResubmissionDate"),
			Map.entry("firstremittancedate", "FirstRemittanceDate"),
			Map.entry("initialdatesettlement", "InitialDateSettlement"));

	private ActivityReportQueryBuilder() {
	}

	static Filter build(
			Long officeId,
			String officeIds,
			String searchOn,
			String fromDate,
			String toDate,
			Integer year,
			Integer month,
			Map<String, String> filters,
			boolean requireInvoice) {

		StringBuilder where = new StringBuilder(" WHERE 1=1");
		Map<String, Object> params = new LinkedHashMap<>();

		if (requireInvoice) {
			where.append(" AND ar.InvoiceNo IS NOT NULL AND TRIM(ar.InvoiceNo) <> ''");
		}

		List<Long> offices = parseOfficeIds(officeId, officeIds);
		if (!offices.isEmpty()) {
			where.append(" AND ar.officeid IN (:officeIds)");
			params.put("officeIds", offices);
		}

		applyDateFilter(where, params, searchOn, fromDate, toDate, year, month);
		applyOptionalFilters(where, params, filters);

		return new Filter(where.toString(), params, offices);
	}

	static List<Long> parseOfficeIds(Long officeId, String officeIds) {
		List<Long> ids = new ArrayList<>();
		if (officeId != null) {
			ids.add(officeId);
		}
		if (hasText(officeIds)) {
			for (String part : officeIds.split(",")) {
				if (!hasText(part)) {
					continue;
				}
				try {
					ids.add(Long.parseLong(part.trim()));
				} catch (NumberFormatException ex) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid officeIds: " + part);
				}
			}
		}
		return ids.stream().distinct().toList();
	}

	private static void applyDateFilter(
			StringBuilder where,
			Map<String, Object> params,
			String searchOn,
			String fromDate,
			String toDate,
			Integer year,
			Integer month) {

		boolean hasRange = hasText(fromDate) || hasText(toDate);
		boolean hasYearMonth = year != null || month != null;
		if (hasRange && hasYearMonth) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Do not mix fromDate/toDate with year/month");
		}
		if (hasRange) {
			if (!hasText(fromDate) || !hasText(toDate)) {
				throw new ResponseStatusException(
						HttpStatus.BAD_REQUEST,
						"fromDate and toDate are required together (yyyy-MM-dd)");
			}
			String column = resolveSearchOn(hasText(searchOn) ? searchOn : "TransactionDate");
			where.append(" AND DATE(ar.").append(column).append(") >= :fromDate");
			where.append(" AND DATE(ar.").append(column).append(") <= :toDate");
			params.put("fromDate", parseIsoDate(fromDate, "fromDate"));
			params.put("toDate", parseIsoDate(toDate, "toDate"));
			return;
		}
		if (year != null) {
			where.append(" AND ar.ClaimYear = :claimYear");
			params.put("claimYear", year);
			if (month != null) {
				if (month < 1 || month > 12) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "month must be 1-12");
				}
				where.append(" AND ar.ClaimMonth = :claimMonth");
				params.put("claimMonth", month);
			}
		}
	}

	private static void applyOptionalFilters(StringBuilder where, Map<String, Object> params, Map<String, String> filters) {
		if (filters == null) {
			return;
		}
		equals(where, params, filters, "payerId", "ar.PayerID");
		equals(where, params, filters, "receiverId", "ar.ReceiverID");
		equals(where, params, filters, "facilityId", "ar.FacilityID");
		equals(where, params, filters, "memberId", "ar.MemberID");
		equals(where, params, filters, "patientId", "ar.PatientID");
		equals(where, params, filters, "idPayer", "ar.IDPayer");
		equals(where, params, filters, "claimStatus", "ar.ClaimStatus");
		equals(where, params, filters, "paymentStatus", "ar.PaymentStatus");
		equals(where, params, filters, "receiptStatus", "ar.ReceiptStatus");
		equals(where, params, filters, "resubmissionType", "ar.ResubmissionType");
		equals(where, params, filters, "patientType", "ar.patient_type");
		equals(where, params, filters, "clinicianId", "ar.Clinician");
		equals(where, params, filters, "invoiceNo", "ar.InvoiceNo");

		like(where, params, filters, "payer", "ar.Payer_Name");
		like(where, params, filters, "payerName", "ar.Payer_Name");
		like(where, params, filters, "receiverName", "ar.Receiver_Name");
		like(where, params, filters, "facilityName", "ar.Facility_Name");
		like(where, params, filters, "clinicianName", "ar.Clinician_Name");
		like(where, params, filters, "encounterType", "ar.EncounterType");
		like(where, params, filters, "encounterTypeDescription", "ar.encounter_type_description");
		like(where, params, filters, "cpt", "ar.CPTCode");
		like(where, params, filters, "cptCode", "ar.CPTCode");
		like(where, params, filters, "claimNo", "ar.InvoiceNo");
		like(where, params, filters, "invoiceNoLike", "ar.InvoiceNo");

		String clinician = filters.get("clinician");
		if (hasText(clinician)) {
			String pattern = likePattern(clinician);
			where.append(" AND (ar.Clinician LIKE :clinician OR ar.Clinician_Name LIKE :clinician)");
			params.put("clinician", pattern);
		}

		String denial = filters.get("denial");
		if (hasText(denial)) {
			String pattern = likePattern(denial);
			where.append(" AND (ar.LastDenailCode LIKE :denial")
					.append(" OR ar.InitialDenialCode LIKE :denial")
					.append(" OR ar.DenialComment LIKE :denial")
					.append(" OR ar.DenialCategory LIKE :denial)");
			params.put("denial", pattern);
		}
	}

	private static void equals(StringBuilder where, Map<String, Object> params, Map<String, String> filters,
			String key, String column) {
		String value = filters.get(key);
		if (!hasText(value)) {
			return;
		}
		where.append(" AND ").append(column).append(" = :").append(key);
		params.put(key, value.trim());
	}

	private static void like(StringBuilder where, Map<String, Object> params, Map<String, String> filters,
			String key, String column) {
		String value = filters.get(key);
		if (!hasText(value)) {
			return;
		}
		where.append(" AND ").append(column).append(" LIKE :").append(key);
		params.put(key, likePattern(value));
	}

	private static String likePattern(String value) {
		String pattern = value.trim();
		if (!pattern.contains("%")) {
			pattern = "%" + pattern + "%";
		}
		return pattern;
	}

	private static String resolveSearchOn(String searchOn) {
		String key = searchOn.trim().toLowerCase(Locale.ROOT);
		String column = SEARCH_ON_COLUMNS.get(key);
		if (column == null && DATE_COLUMNS.contains(searchOn.trim())) {
			column = searchOn.trim();
		}
		if (column == null) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Invalid searchOn. Use TransactionDate, EncounterStartDate, LastRemittanceDate, or another date column");
		}
		return column;
	}

	private static LocalDate parseIsoDate(String value, String field) {
		try {
			return LocalDate.parse(value.trim());
		} catch (DateTimeParseException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " must be yyyy-MM-dd");
		}
	}

	private static boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

	record Filter(String whereSql, Map<String, Object> params, List<Long> officeIds) {
	}
}
