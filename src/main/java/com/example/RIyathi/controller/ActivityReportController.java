package com.example.RIyathi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.RIyathi.service.ActivityReportService;
import com.example.RIyathi.service.ReportExportWriter;
import com.example.RIyathi.service.ReportQueryParams;

@RestController
@RequestMapping("/api/activity-report")
public class ActivityReportController {

	private final ActivityReportService activityReportService;
	private final ReportExportWriter reportExportWriter;

	public ActivityReportController(
			ActivityReportService activityReportService,
			ReportExportWriter reportExportWriter) {
		this.activityReportService = activityReportService;
		this.reportExportWriter = reportExportWriter;
	}

	@GetMapping
	public ResponseEntity<Object> list(
			@ModelAttribute ReportQueryParams query,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "500") int size) {
		return ResponseEntity.ok(activityReportService.list(query, page, size));
	}

	@GetMapping("/export/csv")
	public ResponseEntity<byte[]> exportCsv(@ModelAttribute ReportQueryParams query) {
		return reportExportWriter.csv("activity-report", activityReportService.exportRows(query));
	}

	@GetMapping("/export/excel")
	public ResponseEntity<byte[]> exportExcel(@ModelAttribute ReportQueryParams query) {
		return reportExportWriter.excel("activity-report", "Activity Report", activityReportService.exportRows(query));
	}

	@PostMapping("/generate")
	public ResponseEntity<Object> generateAll() {
		return ResponseEntity.ok(activityReportService.generate(null));
	}

	@GetMapping("/generate")
	public ResponseEntity<Object> generateAllGet() {
		return ResponseEntity.ok(activityReportService.generate(null));
	}

	@PostMapping("/generate/{officeId}")
	public ResponseEntity<Object> generateByOffice(@PathVariable Long officeId) {
		return ResponseEntity.ok(activityReportService.generate(officeId));
	}

	@GetMapping("/generate/{officeId}")
	public ResponseEntity<Object> generateByOfficeGet(@PathVariable Long officeId) {
		return ResponseEntity.ok(activityReportService.generate(officeId));
	}
}
