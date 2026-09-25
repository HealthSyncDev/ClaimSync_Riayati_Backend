package com.example.RIyathi.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Component
public class ReportExportWriter {

	private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final Set<String> SKIP_HEADERS = Set.of(
			"id",
			"officeid",
			"officeId",
			"facilityGroupId",
			"isTotalRow");

	public ResponseEntity<byte[]> csv(String filename, List<Map<String, Object>> rows) {
		List<String> headers = headers(rows);
		StringBuilder csv = new StringBuilder();
		csv.append('\uFEFF');
		csv.append(join(headers)).append('\n');
		for (Map<String, Object> row : rows) {
			List<String> values = new ArrayList<>();
			for (String header : headers) {
				values.add(csvValue(row.get(header)));
			}
			csv.append(join(values)).append('\n');
		}
		return file(filename + ".csv", "text/csv; charset=UTF-8", csv.toString().getBytes(StandardCharsets.UTF_8));
	}

	public ResponseEntity<byte[]> excel(String filename, String sheetName, List<Map<String, Object>> rows) {
		List<String> headers = headers(rows);
		try (SXSSFWorkbook workbook = new SXSSFWorkbook(100);
				ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet(sheetName);
			Row headerRow = sheet.createRow(0);
			for (int i = 0; i < headers.size(); i++) {
				headerRow.createCell(i).setCellValue(headers.get(i));
			}
			int rowIndex = 1;
			for (Map<String, Object> row : rows) {
				Row excelRow = sheet.createRow(rowIndex++);
				for (int i = 0; i < headers.size(); i++) {
					setCell(excelRow.createCell(i), row.get(headers.get(i)));
				}
			}
			workbook.write(output);
			workbook.dispose();
			return file(
					filename + ".xlsx",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
					output.toByteArray());
		} catch (IOException ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create Excel file");
		}
	}

	private List<String> headers(List<Map<String, Object>> rows) {
		Set<String> headers = new LinkedHashSet<>();
		for (Map<String, Object> row : rows) {
			for (String key : row.keySet()) {
				if (!SKIP_HEADERS.contains(key)) {
					headers.add(key);
				}
			}
		}
		return new ArrayList<>(headers);
	}

	private String join(List<String> values) {
		return String.join(",", values);
	}

	private String csvValue(Object value) {
		if (value == null) {
			return "";
		}
		String text = display(value);
		if (text.contains("\"") || text.contains(",") || text.contains("\n") || text.contains("\r")) {
			return "\"" + text.replace("\"", "\"\"") + "\"";
		}
		return text;
	}

	private void setCell(Cell cell, Object value) {
		if (value == null) {
			cell.setBlank();
			return;
		}
		if (value instanceof Number number) {
			cell.setCellValue(number.doubleValue());
			return;
		}
		if (value instanceof BigDecimal decimal) {
			cell.setCellValue(decimal.doubleValue());
			return;
		}
		if (value instanceof Boolean bool) {
			cell.setCellValue(bool);
			return;
		}
		cell.setCellValue(display(value));
	}

	private String display(Object value) {
		if (value instanceof LocalDateTime dateTime) {
			return dateTime.format(DATE_TIME);
		}
		if (value instanceof LocalDate date) {
			return date.format(DATE);
		}
		return String.valueOf(value);
	}

	private ResponseEntity<byte[]> file(String filename, String contentType, byte[] body) {
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
				.contentType(MediaType.parseMediaType(contentType))
				.body(body);
	}
}
