package com.example.RIyathi.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.RIyathi.entity.Office;
import com.example.RIyathi.entity.RemittanceActivity;
import com.example.RIyathi.entity.RemittanceClaim;
import com.example.RIyathi.entity.RemittanceEncounter;
import com.example.RIyathi.entity.RemittanceHeader;
import com.example.RIyathi.entity.RemittanceTransaction;
import com.example.RIyathi.entity.SubmissionActivity;
import com.example.RIyathi.entity.SubmissionClaim;
import com.example.RIyathi.entity.SubmissionDiagnosis;
import com.example.RIyathi.entity.SubmissionDxinfo;
import com.example.RIyathi.entity.SubmissionEncounter;
import com.example.RIyathi.entity.SubmissionHeader;
import com.example.RIyathi.entity.SubmissionObservation;
import com.example.RIyathi.entity.SubmissionResubmission;
import com.example.RIyathi.entity.SubmissionTransaction;
import com.example.RIyathi.repository.OfficeRepository;
import com.example.RIyathi.repository.RemittanceActivityRepository;
import com.example.RIyathi.repository.RemittanceClaimRepository;
import com.example.RIyathi.repository.RemittanceEncounterRepository;
import com.example.RIyathi.repository.RemittanceHeaderRepository;
import com.example.RIyathi.repository.RemittanceTransactionRepository;
import com.example.RIyathi.repository.SubmissionActivityRepository;
import com.example.RIyathi.repository.SubmissionClaimRepository;
import com.example.RIyathi.repository.SubmissionDiagnosisRepository;
import com.example.RIyathi.repository.SubmissionDxinfoRepository;
import com.example.RIyathi.repository.SubmissionEncounterRepository;
import com.example.RIyathi.repository.SubmissionHeaderRepository;
import com.example.RIyathi.repository.SubmissionObservationRepository;
import com.example.RIyathi.repository.SubmissionResubmissionRepository;
import com.example.RIyathi.repository.SubmissionTransactionRepository;

@Service
public class ExtractionService {

	private static final String CLAIM_VIEW_PATH = "/api/Claim/View";
	private static final int DIRECTION_REMITTANCE = 0;
	private static final int DIRECTION_SUBMISSION = 1;
	private static final DateTimeFormatter[] DATE_FORMATS = new DateTimeFormatter[] {
			DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),
			DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
			DateTimeFormatter.ofPattern("dd/MM/yyyy")
	};

	@Value("${tmb.api.base-url}")
	private String baseUrl;

	private final RestTemplate restTemplate;
	private final TransactionTemplate transactionTemplate;
	private final OfficeRepository officeRepository;
	private final RemittanceTransactionRepository remittanceTransactionRepository;
	private final SubmissionTransactionRepository submissionTransactionRepository;
	private final RemittanceHeaderRepository remittanceHeaderRepository;
	private final RemittanceClaimRepository remittanceClaimRepository;
	private final RemittanceEncounterRepository remittanceEncounterRepository;
	private final RemittanceActivityRepository remittanceActivityRepository;
	private final SubmissionHeaderRepository submissionHeaderRepository;
	private final SubmissionClaimRepository submissionClaimRepository;
	private final SubmissionEncounterRepository submissionEncounterRepository;
	private final SubmissionDiagnosisRepository submissionDiagnosisRepository;
	private final SubmissionDxinfoRepository submissionDxinfoRepository;
	private final SubmissionActivityRepository submissionActivityRepository;
	private final SubmissionObservationRepository submissionObservationRepository;
	private final SubmissionResubmissionRepository submissionResubmissionRepository;

	public ExtractionService(
			RestTemplate restTemplate,
			PlatformTransactionManager transactionManager,
			OfficeRepository officeRepository,
			RemittanceTransactionRepository remittanceTransactionRepository,
			SubmissionTransactionRepository submissionTransactionRepository,
			RemittanceHeaderRepository remittanceHeaderRepository,
			RemittanceClaimRepository remittanceClaimRepository,
			RemittanceEncounterRepository remittanceEncounterRepository,
			RemittanceActivityRepository remittanceActivityRepository,
			SubmissionHeaderRepository submissionHeaderRepository,
			SubmissionClaimRepository submissionClaimRepository,
			SubmissionEncounterRepository submissionEncounterRepository,
			SubmissionDiagnosisRepository submissionDiagnosisRepository,
			SubmissionDxinfoRepository submissionDxinfoRepository,
			SubmissionActivityRepository submissionActivityRepository,
			SubmissionObservationRepository submissionObservationRepository,
			SubmissionResubmissionRepository submissionResubmissionRepository) {
		this.restTemplate = restTemplate;
		this.transactionTemplate = new TransactionTemplate(transactionManager);
		this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		this.officeRepository = officeRepository;
		this.remittanceTransactionRepository = remittanceTransactionRepository;
		this.submissionTransactionRepository = submissionTransactionRepository;
		this.remittanceHeaderRepository = remittanceHeaderRepository;
		this.remittanceClaimRepository = remittanceClaimRepository;
		this.remittanceEncounterRepository = remittanceEncounterRepository;
		this.remittanceActivityRepository = remittanceActivityRepository;
		this.submissionHeaderRepository = submissionHeaderRepository;
		this.submissionClaimRepository = submissionClaimRepository;
		this.submissionEncounterRepository = submissionEncounterRepository;
		this.submissionDiagnosisRepository = submissionDiagnosisRepository;
		this.submissionDxinfoRepository = submissionDxinfoRepository;
		this.submissionActivityRepository = submissionActivityRepository;
		this.submissionObservationRepository = submissionObservationRepository;
		this.submissionResubmissionRepository = submissionResubmissionRepository;
	}

	public Object extractRemittance(String id, Long officeId, String username, String password) {
		List<Office> offices = resolveOffices(officeId, username, password);
		if (id != null) {
			return extractSafely(id, DIRECTION_REMITTANCE, offices);
		}
		List<Map<String, Object>> results = new ArrayList<>();
		List<RemittanceTransaction> pending = pendingOfficeId(offices) != null
				? remittanceTransactionRepository.findByIsExtractedAndOfficeId(0, pendingOfficeId(offices))
				: remittanceTransactionRepository.findByIsExtracted(0);
		for (RemittanceTransaction tx : pending) {
			results.add(extractSafely(tx.getId(), DIRECTION_REMITTANCE, officesForTransaction(tx.getOfficeId(), offices)));
		}
		return results;
	}

	public Object extractSubmission(String id, Long officeId, String username, String password) {
		List<Office> offices = resolveOffices(officeId, username, password);
		if (id != null) {
			return extractSafely(id, DIRECTION_SUBMISSION, offices);
		}
		List<Map<String, Object>> results = new ArrayList<>();
		List<SubmissionTransaction> pending = pendingOfficeId(offices) != null
				? submissionTransactionRepository.findByIsExtractedAndOfficeId(0, pendingOfficeId(offices))
				: submissionTransactionRepository.findByIsExtracted(0);
		for (SubmissionTransaction tx : pending) {
			results.add(extractSafely(tx.getId(), DIRECTION_SUBMISSION, officesForTransaction(tx.getOfficeId(), offices)));
		}
		return results;
	}

	private Map<String, Object> extractSafely(String id, int direction, List<Office> offices) {
		try {
			return transactionTemplate.execute(status -> extractOne(id, direction, offices));
		} catch (Exception ex) {
			Map<String, Object> result = new LinkedHashMap<>();
			result.put("id", id);
			result.put("direction", direction);
			result.put("success", false);
			result.put("error", exceptionMessage(ex));
			return result;
		}
	}

	private Map<String, Object> extractOne(String id, int direction, List<Office> offices) {
		if (id == null || id.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction ID is required");
		}

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("id", id);
		result.put("direction", direction);

		if (direction == DIRECTION_REMITTANCE) {
			RemittanceTransaction tx = findRemittanceTransaction(id, offices);
			if (Integer.valueOf(1).equals(tx.getIsExtracted())) {
				result.put("success", true);
				result.put("skipped", true);
				result.put("message", "Already extracted");
				return result;
			}
			ClaimViewResult view = callClaimView(id, direction, officesForTransaction(tx.getOfficeId(), offices));
			saveRemittance(tx, view.payload(), view.office());
			tx.setIsExtracted(1);
			remittanceTransactionRepository.save(tx);
			result.put("officeId", tx.getOfficeId());
		} else if (direction == DIRECTION_SUBMISSION) {
			SubmissionTransaction tx = findSubmissionTransaction(id, offices);
			if (Integer.valueOf(1).equals(tx.getIsExtracted())) {
				result.put("success", true);
				result.put("skipped", true);
				result.put("message", "Already extracted");
				return result;
			}
			ClaimViewResult view = callClaimView(id, direction, officesForTransaction(tx.getOfficeId(), offices));
			saveSubmission(tx, view.payload(), view.office());
			tx.setIsExtracted(1);
			submissionTransactionRepository.save(tx);
			result.put("officeId", tx.getOfficeId());
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "direction must be 0 (remittance) or 1 (submission)");
		}
		result.put("success", true);
		result.put("isExtracted", 1);
		return result;
	}

	private ClaimViewResult callClaimView(String id, int direction, List<Office> offices) {
		Exception lastError = null;
		for (Office office : offices) {
			try {
				String url = UriComponentsBuilder.fromUriString(baseUrl + CLAIM_VIEW_PATH)
						.queryParam("id", id)
						.queryParam("direction", direction)
						.build(false)
						.toUriString();

				HttpHeaders headers = tmbAuthHeaders(office);

				HttpEntity<Void> entity = new HttpEntity<>(headers);
				ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
				Office resolved = resolveOfficeIdentity(office);
				return new ClaimViewResult(unwrapPayload(response.getBody()), resolved);
			} catch (Exception ex) {
				lastError = ex;
			}
		}
		throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, exceptionMessage(lastError != null
				? lastError
				: new RuntimeException("Claim/View failed for id " + id)));
	}

	private void saveRemittance(RemittanceTransaction tx, Map<String, Object> payload, Office office) {
		Map<String, Object> headerMap = asMap(first(payload, "Header", "header"));
		if (headerMap.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Claim/View response has no Header");
		}

		RemittanceHeader header = new RemittanceHeader();
		header.setSenderId(requiredText(headerMap, tx.getSenderId(), "SenderID", "senderID"));
		header.setReceiverId(requiredText(headerMap, tx.getReceiverId(), "ReceiverID", "receiverID"));
		header.setTransactionDate(dateValue(headerMap, "TransactionDate", "transactionDate"));
		header.setRecordCount(intValue(headerMap, "RecordCount", "recordCount"));
		header.setDispositionFlag(text(headerMap, "DispositionFlag", "dispositionFlag"));
		header.setPayerId(text(headerMap, "PayerID", "payerID"));
		header.setTransactionTableId(tx.getTableId());
		header.setOfficeId(office != null && office.getId() != null ? office.getId() : tx.getOfficeId());
		header = remittanceHeaderRepository.save(header);

		for (Map<String, Object> claimMap : asList(first(payload, "Claim", "claim"))) {
			RemittanceClaim claim = new RemittanceClaim();
			claim.setClaimId(text(claimMap, "ID", "Id", "id"));
			claim.setIdPayer(text(claimMap, "IDPayer", "IdPayer"));
			claim.setProviderId(text(claimMap, "ProviderID", "ProviderId"));
			claim.setDenialCode(text(claimMap, "DenialCode"));
			claim.setPaymentReference(text(claimMap, "PaymentReference"));
			claim.setDateSettlement(dateValue(claimMap, "DateSettlement"));
			claim.setHeaderTableId(header.getTableId());
			claim = remittanceClaimRepository.save(claim);

			Map<String, Object> encounterMap = asMap(first(claimMap, "Encounter", "encounter"));
			if (!encounterMap.isEmpty()) {
				RemittanceEncounter encounter = new RemittanceEncounter();
				encounter.setFacilityId(text(encounterMap, "FacilityID", "FacilityId"));
				encounter.setClaimTableId(claim.getTableId());
				remittanceEncounterRepository.save(encounter);
			}

			for (Map<String, Object> activityMap : asList(first(claimMap, "Activity", "activity"))) {
				RemittanceActivity activity = new RemittanceActivity();
				activity.setActivityId(text(activityMap, "ID", "Id", "id"));
				activity.setStart(dateValue(activityMap, "Start"));
				activity.setType(text(activityMap, "Type"));
				activity.setCode(text(activityMap, "Code"));
				activity.setQuantity(text(activityMap, "Quantity"));
				activity.setNet(decimalValue(activityMap, "Net"));
				activity.setList(text(activityMap, "List"));
				activity.setClinician(text(activityMap, "Clinician"));
				activity.setPriorAuthorizationId(text(activityMap, "PriorAuthorizationID", "PriorAuthorizationId"));
				activity.setGross(decimalValue(activityMap, "Gross"));
				activity.setPatientShare(decimalValue(activityMap, "PatientShare"));
				activity.setActivityPenalty(text(activityMap, "ActivityPenalty"));
				activity.setPaymentAmount(decimalValue(activityMap, "PaymentAmount"));
				activity.setDenialCode(text(activityMap, "DenialCode"));
				activity.setComments(text(activityMap, "Comments", "Comment"));
				activity.setClaimTableId(claim.getTableId());
				remittanceActivityRepository.save(activity);
			}
		}
	}

	private void saveSubmission(SubmissionTransaction tx, Map<String, Object> payload, Office office) {
		Map<String, Object> headerMap = asMap(first(payload, "Header", "header"));
		if (headerMap.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Claim/View response has no Header");
		}

		SubmissionHeader header = new SubmissionHeader();
		header.setSenderId(requiredText(headerMap, tx.getSenderId(), "SenderID", "senderID"));
		header.setReceiverId(requiredText(headerMap, tx.getReceiverId(), "ReceiverID", "receiverID"));
		header.setTransactionDate(dateValue(headerMap, "TransactionDate", "transactionDate"));
		header.setRecordCount(intValue(headerMap, "RecordCount", "recordCount"));
		header.setDispositionFlag(text(headerMap, "DispositionFlag", "dispositionFlag"));
		header.setPayerId(text(headerMap, "PayerID", "payerID"));
		header.setTransactionTableId(tx.getTableId());
		header.setOfficeId(office != null && office.getId() != null ? office.getId() : tx.getOfficeId());
		header = submissionHeaderRepository.save(header);

		for (Map<String, Object> claimMap : asList(first(payload, "Claim", "claim"))) {
			SubmissionClaim claim = new SubmissionClaim();
			claim.setClaimId(text(claimMap, "ID", "Id", "id"));
			claim.setIdPayer(text(claimMap, "IDPayer", "IdPayer"));
			claim.setType(text(claimMap, "Type"));
			claim.setMemberId(text(claimMap, "MemberID", "MemberId"));
			claim.setDispensedId(text(claimMap, "DispensedID", "DispensedId"));
			claim.setProviderId(text(claimMap, "ProviderID", "ProviderId"));
			claim.setWeight(decimalValue(claimMap, "Weight"));
			claim.setNationalIdNumber(text(claimMap, "NationalIDNumber", "NationalIdNumber"));
			claim.setGross(decimalValue(claimMap, "Gross"));
			claim.setPatientShare(decimalValue(claimMap, "PatientShare"));
			claim.setNet(decimalValue(claimMap, "Net"));
			claim.setReferenceNumber(text(claimMap, "ReferenceNumber"));
			claim.setDateOfBirth(dateValue(claimMap, "DateOfBirth"));
			claim.setGender(text(claimMap, "Gender"));
			claim.setHeaderTableId(header.getTableId());
			claim = submissionClaimRepository.save(claim);

			Map<String, Object> encounterMap = asMap(first(claimMap, "Encounter", "encounter"));
			if (!encounterMap.isEmpty()) {
				SubmissionEncounter encounter = new SubmissionEncounter();
				encounter.setFacilityId(text(encounterMap, "FacilityID", "FacilityId"));
				encounter.setType(text(encounterMap, "Type"));
				encounter.setPatientId(text(encounterMap, "PatientID", "PatientId"));
				encounter.setStart(dateValue(encounterMap, "Start"));
				encounter.setEnd(dateValue(encounterMap, "End"));
				encounter.setStartType(text(encounterMap, "StartType"));
				encounter.setEndType(text(encounterMap, "EndType"));
				encounter.setTransferSource(text(encounterMap, "TransferSource"));
				encounter.setTransferDestination(text(encounterMap, "TransferDestination"));
				encounter.setClaimTableId(claim.getTableId());
				submissionEncounterRepository.save(encounter);
			}

			for (Map<String, Object> diagnosisMap : asList(first(claimMap, "Diagnosis", "diagnosis"))) {
				SubmissionDiagnosis diagnosis = new SubmissionDiagnosis();
				diagnosis.setType(text(diagnosisMap, "Type"));
				diagnosis.setCode(text(diagnosisMap, "Code"));
				diagnosis.setClaimTableId(claim.getTableId());
				diagnosis = submissionDiagnosisRepository.save(diagnosis);

				for (Map<String, Object> dxMap : asList(first(diagnosisMap, "Dxinfo", "DxInfo", "dxinfo"))) {
					SubmissionDxinfo dxinfo = new SubmissionDxinfo();
					dxinfo.setType(text(dxMap, "Type"));
					dxinfo.setCode(text(dxMap, "Code"));
					dxinfo.setDiagnosisTableId(diagnosis.getTableId());
					submissionDxinfoRepository.save(dxinfo);
				}
			}

			for (Map<String, Object> activityMap : asList(first(claimMap, "Activity", "activity"))) {
				SubmissionActivity activity = new SubmissionActivity();
				activity.setActivityId(text(activityMap, "ID", "Id", "id"));
				activity.setStart(dateValue(activityMap, "Start"));
				activity.setType(text(activityMap, "Type"));
				activity.setLocation(text(activityMap, "Location"));
				activity.setCode(text(activityMap, "Code"));
				activity.setQuantity(text(activityMap, "Quantity"));
				activity.setNet(decimalValue(activityMap, "Net"));
				activity.setPatientShare(decimalValue(activityMap, "PatientShare"));
				activity.setClinician(text(activityMap, "Clinician"));
				activity.setDuration(text(activityMap, "Duration"));
				activity.setPriorAuthorizationId(text(activityMap, "PriorAuthorizationID", "PriorAuthorizationId"));
				activity.setDispensedActivityId(text(activityMap, "DispensedActivityID", "DispensedActivityId"));
				activity.setClaimTableId(claim.getTableId());
				activity = submissionActivityRepository.save(activity);

				for (Map<String, Object> observationMap : asList(first(activityMap, "Observation", "observation"))) {
					SubmissionObservation observation = new SubmissionObservation();
					observation.setType(text(observationMap, "Type"));
					observation.setCode(text(observationMap, "Code"));
					observation.setValue(text(observationMap, "Value"));
					observation.setValueType(text(observationMap, "ValueType"));
					observation.setActivityTableId(activity.getTableId());
					submissionObservationRepository.save(observation);
				}
			}

			Map<String, Object> resubmissionMap = asMap(first(claimMap, "Resubmission", "resubmission"));
			if (!resubmissionMap.isEmpty()) {
				SubmissionResubmission resubmission = new SubmissionResubmission();
				resubmission.setAttachment(textOrJson(resubmissionMap, "attachment", "Attachment"));
				resubmission.setComment(text(resubmissionMap, "Comment", "Comments"));
				resubmission.setType(text(resubmissionMap, "Type"));
				resubmission.setClaimTableId(claim.getTableId());
				submissionResubmissionRepository.save(resubmission);
			}
		}
	}

	private List<Office> resolveOffices(Long officeId, String username, String password) {
		if (hasText(username) && hasText(password)) {
			Office office = officeRepository.findFirstByUsername(username).orElseGet(Office::new);
			office.setUsername(username);
			office.setPassword(password);
			return List.of(office);
		}
		if (officeId != null) {
			Office office = officeRepository.findById(officeId)
					.orElseThrow(() -> new ResponseStatusException(
							HttpStatus.NOT_FOUND, "Office not found with id: " + officeId));
			return List.of(office);
		}
		List<Office> active = officeRepository.findAll().stream()
				.filter(o -> o.getStatus() != null && o.getStatus() == 1)
				.toList();
		if (active.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No active offices found");
		}
		return active;
	}

	private RemittanceTransaction findRemittanceTransaction(String id, List<Office> offices) {
		Long officeId = pendingOfficeId(offices);
		if (officeId != null) {
			return remittanceTransactionRepository.findFirstByIdAndOfficeId(id, officeId)
					.orElseGet(() -> remittanceTransactionRepository.findFirstById(id)
							.orElseThrow(() -> new ResponseStatusException(
									HttpStatus.NOT_FOUND, "Remittance transaction not found for id: " + id)));
		}
		return remittanceTransactionRepository.findFirstById(id)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "Remittance transaction not found for id: " + id));
	}

	private SubmissionTransaction findSubmissionTransaction(String id, List<Office> offices) {
		Long officeId = pendingOfficeId(offices);
		if (officeId != null) {
			return submissionTransactionRepository.findFirstByIdAndOfficeId(id, officeId)
					.orElseGet(() -> submissionTransactionRepository.findFirstById(id)
							.orElseThrow(() -> new ResponseStatusException(
									HttpStatus.NOT_FOUND, "Submission transaction not found for id: " + id)));
		}
		return submissionTransactionRepository.findFirstById(id)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "Submission transaction not found for id: " + id));
	}

	private List<Office> officesForTransaction(Long transactionOfficeId, List<Office> offices) {
		if (transactionOfficeId == null) {
			return offices;
		}
		return officeRepository.findById(transactionOfficeId)
				.map(List::of)
				.orElse(offices);
	}

	private Long pendingOfficeId(List<Office> offices) {
		if (offices != null && offices.size() == 1) {
			return offices.get(0).getId();
		}
		return null;
	}

	private Office resolveOfficeIdentity(Office office) {
		if (office == null) {
			return null;
		}
		if (office.getId() != null) {
			return office;
		}
		if (hasText(office.getUsername())) {
			return officeRepository.findFirstByUsername(office.getUsername()).orElse(office);
		}
		return office;
	}

	private record ClaimViewResult(Map<String, Object> payload, Office office) {
	}

	private HttpHeaders tmbAuthHeaders(Office office) {
		HttpHeaders headers = new HttpHeaders();
		headers.put("username", List.of(office.getUsername()));
		headers.put("password", List.of(office.getPassword()));
		return headers;
	}

	private boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

	private String exceptionMessage(Exception ex) {
		if (ex == null) {
			return "Unknown error";
		}
		Throwable current = ex;
		while (current.getCause() != null && current.getCause() != current) {
			if (current.getMessage() != null && current.getMessage().contains("Data too long")) {
				return current.getMessage();
			}
			current = current.getCause();
		}
		if (ex instanceof RestClientResponseException restEx) {
			String body = restEx.getResponseBodyAsString();
			return "TMB Claim/View HTTP " + restEx.getStatusCode().value()
					+ (body != null && !body.isBlank() ? ": " + body : ": " + restEx.getStatusText());
		}
		if (ex instanceof ResponseStatusException statusEx && statusEx.getReason() != null) {
			return statusEx.getReason();
		}
		return ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> unwrapPayload(Object body) {
		if (body instanceof List<?> list && !list.isEmpty()) {
			return unwrapPayload(list.get(0));
		}
		if (body instanceof Map<?, ?> map) {
			Map<String, Object> asMap = (Map<String, Object>) map;
			if (asMap.containsKey("Header") || asMap.containsKey("header")
					|| asMap.containsKey("Claim") || asMap.containsKey("claim")) {
				return asMap;
			}
			for (String key : List.of(
					"Entity", "entity",
					"data", "Data",
					"result", "Result",
					"Remittance", "Submission",
					"Claim.Submission", "Claim.Remittance",
					"claimSubmission", "claimRemittance")) {
				if (asMap.containsKey(key) && asMap.get(key) != null) {
					return unwrapPayload(asMap.get(key));
				}
			}
			return asMap;
		}
		throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Unexpected Claim/View response");
	}

	private Object first(Map<String, Object> map, String... keys) {
		if (map == null) {
			return null;
		}
		for (String key : keys) {
			if (map.containsKey(key) && map.get(key) != null) {
				return map.get(key);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> asMap(Object value) {
		if (value instanceof Map<?, ?> map) {
			return (Map<String, Object>) map;
		}
		if (value instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof Map<?, ?>) {
			return (Map<String, Object>) list.get(0);
		}
		return Map.of();
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> asList(Object value) {
		if (value instanceof List<?> list) {
			List<Map<String, Object>> result = new ArrayList<>();
			for (Object item : list) {
				if (item instanceof Map<?, ?>) {
					result.add((Map<String, Object>) item);
				}
			}
			return result;
		}
		if (value instanceof Map<?, ?> map) {
			return List.of((Map<String, Object>) map);
		}
		return List.of();
	}

	private String text(Map<String, Object> map, String... keys) {
		Object value = first(map, keys);
		if (value == null) {
			return null;
		}
		String text = String.valueOf(value);
		return text.isBlank() ? null : text;
	}

	private String requiredText(Map<String, Object> map, String fallback, String... keys) {
		String value = text(map, keys);
		if (value != null) {
			return value;
		}
		if (fallback != null && !fallback.isBlank()) {
			return fallback;
		}
		return "";
	}

	private String textOrJson(Map<String, Object> map, String... keys) {
		Object value = first(map, keys);
		if (value == null) {
			return null;
		}
		if (value instanceof String text) {
			return text.isBlank() ? null : text;
		}
		return String.valueOf(value);
	}

	private Integer intValue(Map<String, Object> map, String... keys) {
		Object value = first(map, keys);
		if (value == null || String.valueOf(value).isBlank()) {
			return null;
		}
		if (value instanceof Number number) {
			return number.intValue();
		}
		try {
			return Integer.parseInt(String.valueOf(value).trim());
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	private BigDecimal decimalValue(Map<String, Object> map, String... keys) {
		Object value = first(map, keys);
		if (value == null || String.valueOf(value).isBlank()) {
			return null;
		}
		if (value instanceof BigDecimal decimal) {
			return decimal;
		}
		if (value instanceof Number number) {
			return BigDecimal.valueOf(number.doubleValue());
		}
		try {
			return new BigDecimal(String.valueOf(value).trim());
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	private LocalDateTime dateValue(Map<String, Object> map, String... keys) {
		String text = text(map, keys);
		if (text == null) {
			return null;
		}
		for (DateTimeFormatter formatter : DATE_FORMATS) {
			try {
				if (formatter == DATE_FORMATS[2]) {
					return java.time.LocalDate.parse(text, formatter).atStartOfDay();
				}
				return LocalDateTime.parse(text, formatter);
			} catch (DateTimeParseException ignored) {
				// try next format
			}
		}
		return null;
	}
}
