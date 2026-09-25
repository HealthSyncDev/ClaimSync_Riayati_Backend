package com.example.RIyathi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.RIyathi.service.ExtractionService;
import com.example.RIyathi.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

	private final TransactionService transactionService;
	private final ExtractionService extractionService;

	public TransactionController(TransactionService transactionService, ExtractionService extractionService) {
		this.transactionService = transactionService;
		this.extractionService = extractionService;
	}

	/**
	 * All active offices:
	 * GET /api/transactions/remittance?fromDate=01/01/2024 12:00&toDate=30/01/2024 12:00
	 *
	 * Specific office:
	 * GET /api/transactions/remittance?fromDate=01/01/2024 12:00&toDate=30/01/2024 12:00&officeId=1
	 */
	@GetMapping("/remittance")
	public ResponseEntity<Object> getRemittanceTransactions(
			@RequestParam String fromDate,
			@RequestParam String toDate,
			@RequestParam(required = false) Long officeId) {
		return ResponseEntity.ok(transactionService.fetchRemittance(fromDate, toDate, officeId));
	}

	/**
	 * All active offices:
	 * GET /api/transactions/submission?fromDate=01/01/2024 12:00&toDate=30/01/2024 12:00
	 *
	 * Specific office:
	 * GET /api/transactions/submission?fromDate=01/01/2024 12:00&toDate=30/01/2024 12:00&officeId=1
	 */
	@GetMapping("/submission")
	public ResponseEntity<Object> getSubmissionTransactions(
			@RequestParam String fromDate,
			@RequestParam String toDate,
			@RequestParam(required = false) Long officeId) {
		return ResponseEntity.ok(transactionService.fetchSubmission(fromDate, toDate, officeId));
	}

	/**
	 * Extract all pending remittance/submission for every office:
	 * POST /api/transactions/extract/remittance
	 * POST /api/transactions/extract/submission
	 *
	 * Extract for one office:
	 * POST /api/transactions/extract/remittance/{officeId}
	 * POST /api/transactions/extract/submission/{officeId}
	 *
	 * Optional id extracts that one TMB transaction.
	 */
	@PostMapping("/extract/remittance")
	public ResponseEntity<Object> extractRemittanceAll(
			@RequestParam(required = false) String id) {
		return ResponseEntity.ok(extractionService.extractRemittance(id, null, null, null));
	}

	@GetMapping("/extract/remittance")
	public ResponseEntity<Object> extractRemittanceAllGet(
			@RequestParam(required = false) String id) {
		return ResponseEntity.ok(extractionService.extractRemittance(id, null, null, null));
	}

	@PostMapping("/extract/remittance/{officeId}")
	public ResponseEntity<Object> extractRemittanceByOffice(
			@PathVariable Long officeId,
			@RequestParam(required = false) String id,
			@RequestHeader(value = "username", required = false) String username,
			@RequestHeader(value = "password", required = false) String password) {
		return ResponseEntity.ok(extractionService.extractRemittance(id, officeId, username, password));
	}

	@GetMapping("/extract/remittance/{officeId}")
	public ResponseEntity<Object> extractRemittanceByOfficeGet(
			@PathVariable Long officeId,
			@RequestParam(required = false) String id,
			@RequestHeader(value = "username", required = false) String username,
			@RequestHeader(value = "password", required = false) String password) {
		return ResponseEntity.ok(extractionService.extractRemittance(id, officeId, username, password));
	}

	@PostMapping("/extract/submission")
	public ResponseEntity<Object> extractSubmissionAll(
			@RequestParam(required = false) String id) {
		return ResponseEntity.ok(extractionService.extractSubmission(id, null, null, null));
	}

	@GetMapping("/extract/submission")
	public ResponseEntity<Object> extractSubmissionAllGet(
			@RequestParam(required = false) String id) {
		return ResponseEntity.ok(extractionService.extractSubmission(id, null, null, null));
	}

	@PostMapping("/extract/submission/{officeId}")
	public ResponseEntity<Object> extractSubmissionByOffice(
			@PathVariable Long officeId,
			@RequestParam(required = false) String id,
			@RequestHeader(value = "username", required = false) String username,
			@RequestHeader(value = "password", required = false) String password) {
		return ResponseEntity.ok(extractionService.extractSubmission(id, officeId, username, password));
	}

	@GetMapping("/extract/submission/{officeId}")
	public ResponseEntity<Object> extractSubmissionByOfficeGet(
			@PathVariable Long officeId,
			@RequestParam(required = false) String id,
			@RequestHeader(value = "username", required = false) String username,
			@RequestHeader(value = "password", required = false) String password) {
		return ResponseEntity.ok(extractionService.extractSubmission(id, officeId, username, password));
	}
}
