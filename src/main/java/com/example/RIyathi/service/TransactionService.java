package com.example.RIyathi.service;

import java.time.LocalDate;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.RIyathi.entity.Office;
import com.example.RIyathi.entity.RemittanceTransaction;
import com.example.RIyathi.entity.SubmissionTransaction;
import com.example.RIyathi.repository.OfficeRepository;
import com.example.RIyathi.repository.RemittanceTransactionRepository;
import com.example.RIyathi.repository.SubmissionTransactionRepository;

@Service
public class TransactionService {

	private static final String CLAIM_SEARCH_PATH = "/api/Claim/Search";
	private static final int DOWNLOADED = 2;
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
	private final OfficeRepository officeRepository;
	private final RemittanceTransactionRepository remittanceTransactionRepository;
	private final SubmissionTransactionRepository submissionTransactionRepository;

	public TransactionService(
			RestTemplate restTemplate,
			OfficeRepository officeRepository,
			RemittanceTransactionRepository remittanceTransactionRepository,
			SubmissionTransactionRepository submissionTransactionRepository) {
		this.restTemplate = restTemplate;
		this.officeRepository = officeRepository;
		this.remittanceTransactionRepository = remittanceTransactionRepository;
		this.submissionTransactionRepository = submissionTransactionRepository;
	}

	@Transactional
	public Object fetchRemittance(String fromDate, String toDate, Long officeId) {
		return fetchForOffices(fromDate, toDate, DIRECTION_REMITTANCE, officeId);
	}

	@Transactional
	public Object fetchSubmission(String fromDate, String toDate, Long officeId) {
		return fetchForOffices(fromDate, toDate, DIRECTION_SUBMISSION, officeId);
	}

	private Object fetchForOffices(String fromDate, String toDate, int direction, Long officeId) {
		List<Office> offices = resolveOffices(officeId);

		List<Map<String, Object>> results = new ArrayList<>();
		for (Office office : offices) {
			Map<String, Object> officeResult = new LinkedHashMap<>();
			officeResult.put("officeId", office.getId());
			officeResult.put("facilityName", office.getFacilityName());
			officeResult.put("facilityId", office.getFacilityId());
			try {
				Object data = callTmbClaimSearch(fromDate, toDate, direction, office);
				int saved = saveTransactions(data, direction, office);
				officeResult.put("savedCount", saved);
				officeResult.put("data", data);
				officeResult.put("success", true);
			} catch (Exception ex) {
				officeResult.put("success", false);
				officeResult.put("error", ex.getMessage());
			}
			results.add(officeResult);
		}

		if (officeId != null && results.size() == 1) {
			return results.get(0);
		}
		return results;
	}

	private int saveTransactions(Object data, int direction, Office office) {
		int saved = 0;
		for (Map<String, Object> row : asList(data)) {
			String tmbId = text(row, "ID", "Id", "id");
			if (tmbId == null) {
				continue;
			}
			if (direction == DIRECTION_REMITTANCE) {
				if (remittanceTransactionRepository.findFirstByIdAndOfficeId(tmbId, office.getId()).isPresent()) {
					continue;
				}
				RemittanceTransaction tx = new RemittanceTransaction();
				tx.setId(tmbId);
				tx.setSenderId(textOrEmpty(row, "SenderID", "SenderId"));
				tx.setReceiverId(textOrEmpty(row, "ReceiverID", "ReceiverId"));
				tx.setRecordCount(intValue(row, "RecordCount"));
				tx.setTransactionDate(dateValue(row, "TransactionDate"));
				tx.setCreationDate(dateValue(row, "CreationDate"));
				tx.setDownloaded(boolValue(row, "Downloaded"));
				tx.setDownloadedDate(dateValue(row, "DownloadedDate"));
				tx.setDownloadedDateGeneratedString(text(row, "DownloadedDateGeneratedString"));
				tx.setIsExtracted(0);
				tx.setOfficeId(office.getId());
				remittanceTransactionRepository.save(tx);
				saved++;
			} else {
				if (submissionTransactionRepository.findFirstByIdAndOfficeId(tmbId, office.getId()).isPresent()) {
					continue;
				}
				SubmissionTransaction tx = new SubmissionTransaction();
				tx.setId(tmbId);
				tx.setSenderId(textOrEmpty(row, "SenderID", "SenderId"));
				tx.setReceiverId(textOrEmpty(row, "ReceiverID", "ReceiverId"));
				tx.setRecordCount(intValue(row, "RecordCount"));
				tx.setTransactionDate(dateValue(row, "TransactionDate"));
				tx.setCreationDate(dateValue(row, "CreationDate"));
				tx.setDownloaded(boolValue(row, "Downloaded"));
				tx.setDownloadedDate(dateValue(row, "DownloadedDate"));
				tx.setDownloadedDateGeneratedString(text(row, "DownloadedDateGeneratedString"));
				tx.setIsExtracted(0);
				tx.setOfficeId(office.getId());
				submissionTransactionRepository.save(tx);
				saved++;
			}
		}
		return saved;
	}

	private List<Office> resolveOffices(Long officeId) {
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

	private Object callTmbClaimSearch(String fromDate, String toDate, int direction, Office office) {
		String url = UriComponentsBuilder.fromUriString(baseUrl + CLAIM_SEARCH_PATH)
				.queryParam("direction", direction)
				.queryParam("fromDate", fromDate)
				.queryParam("toDate", toDate)
				.queryParam("downloaded", DOWNLOADED)
				.build(false)
				.toUriString();

		HttpHeaders headers = new HttpHeaders();
		headers.put("username", List.of(office.getUsername()));
		headers.put("password", List.of(office.getPassword()));

		HttpEntity<Void> entity = new HttpEntity<>(headers);
		ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
		return response.getBody();
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
			Map<String, Object> asMap = (Map<String, Object>) map;
			for (String key : List.of(
					"Entities", "entities",
					"data", "Data",
					"result", "Result",
					"items", "Items",
					"Transactions", "transactions")) {
				if (asMap.containsKey(key)) {
					return asList(asMap.get(key));
				}
			}
			if (asMap.containsKey("ID") || asMap.containsKey("Id") || asMap.containsKey("id")) {
				return List.of(asMap);
			}
		}
		return List.of();
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

	private String text(Map<String, Object> map, String... keys) {
		Object value = first(map, keys);
		if (value == null) {
			return null;
		}
		String text = String.valueOf(value);
		return text.isBlank() ? null : text;
	}

	private String textOrEmpty(Map<String, Object> map, String... keys) {
		String value = text(map, keys);
		return value != null ? value : "";
	}

	private Integer intValue(Map<String, Object> map, String... keys) {
		Object value = first(map, keys);
		if (value == null || String.valueOf(value).isBlank()) {
			return 0;
		}
		if (value instanceof Number number) {
			return number.intValue();
		}
		try {
			return Integer.parseInt(String.valueOf(value).trim());
		} catch (NumberFormatException ex) {
			return 0;
		}
	}

	private Boolean boolValue(Map<String, Object> map, String... keys) {
		Object value = first(map, keys);
		if (value == null) {
			return false;
		}
		if (value instanceof Boolean bool) {
			return bool;
		}
		return Boolean.parseBoolean(String.valueOf(value));
	}

	private LocalDateTime dateValue(Map<String, Object> map, String... keys) {
		String text = text(map, keys);
		if (text == null) {
			return null;
		}
		for (DateTimeFormatter formatter : DATE_FORMATS) {
			try {
				if (formatter == DATE_FORMATS[2]) {
					return LocalDate.parse(text, formatter).atStartOfDay();
				}
				return LocalDateTime.parse(text, formatter);
			} catch (DateTimeParseException ignored) {
				// try next format
			}
		}
		return null;
	}
}
