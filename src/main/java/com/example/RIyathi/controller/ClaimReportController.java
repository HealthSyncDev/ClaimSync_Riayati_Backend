package com.example.RIyathi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.RIyathi.service.ClaimReportService;
import com.example.RIyathi.service.ReportExportWriter;
import com.example.RIyathi.service.ReportQueryParams;

@RestController
@RequestMapping("/api/claim-report")
public class ClaimReportController {

	private final ClaimReportService claimReportService;
	private final ReportExportWriter reportExportWriter;

	public ClaimReportController(
			ClaimReportService claimReportService,
			ReportExportWriter reportExportWriter) {
		this.claimReportService = claimReportService;
		this.reportExportWriter = reportExportWriter;
	}

	@GetMapping
	public ResponseEntity<Object> list(
			@ModelAttribute ReportQueryParams query,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "500") int size) {
		return ResponseEntity.ok(claimReportService.list(query, page, size));
	}

	@GetMapping("/export/csv")
	public ResponseEntity<byte[]> exportCsv(@ModelAttribute ReportQueryParams query) {
		return reportExportWriter.csv("claim-report", claimReportService.exportRows(query));
	}

	@GetMapping("/export/excel")
	public ResponseEntity<byte[]> exportExcel(@ModelAttribute ReportQueryParams query) {
		return reportExportWriter.excel("claim-report", "Claim Report", claimReportService.exportRows(query));
	}
}
