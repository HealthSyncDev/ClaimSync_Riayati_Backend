package com.example.RIyathi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.RIyathi.entity.Office;
import com.example.RIyathi.service.OfficeService;

@RestController
@RequestMapping("/api/offices")
public class OfficeController {

	private final OfficeService officeService;

	public OfficeController(OfficeService officeService) {
		this.officeService = officeService;
	}

	@PostMapping
	public ResponseEntity<Office> insert(@RequestBody Office office) {
		return ResponseEntity.status(HttpStatus.CREATED).body(officeService.insert(office));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Office> update(@PathVariable Long id, @RequestBody Office office) {
		return ResponseEntity.ok(officeService.update(id, office));
	}

	@GetMapping
	public ResponseEntity<List<Office>> getAll() {
		return ResponseEntity.ok(officeService.getAll());
	}

	@GetMapping("/active")
	public ResponseEntity<Object> getActive() {
		return ResponseEntity.ok(officeService.getActiveOffices());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Office> getById(@PathVariable Long id) {
		return ResponseEntity.ok(officeService.getById(id));
	}

	@PutMapping("/{id}/inactive")
	public ResponseEntity<Office> setInactive(@PathVariable Long id) {
		return ResponseEntity.ok(officeService.setInactive(id));
	}

	@PutMapping("/{id}/active")
	public ResponseEntity<Office> setActive(@PathVariable Long id) {
		return ResponseEntity.ok(officeService.setActive(id));
	}
}
