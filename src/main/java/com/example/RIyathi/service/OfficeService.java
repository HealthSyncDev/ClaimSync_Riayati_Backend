package com.example.RIyathi.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.RIyathi.entity.Office;
import com.example.RIyathi.repository.OfficeRepository;

@Service
public class OfficeService {

	private final OfficeRepository officeRepository;

	public OfficeService(OfficeRepository officeRepository) {
		this.officeRepository = officeRepository;
	}

	public Office insert(Office office) {
		office.setId(null);
		if (office.getStatus() == null) {
			office.setStatus(1);
		}
		office.setStatusDate(LocalDateTime.now());
		return officeRepository.save(office);
	}

	public Office update(Long id, Office office) {
		Office existing = getById(id);
		existing.setFacilityName(office.getFacilityName());
		existing.setFacilityId(office.getFacilityId());
		existing.setUsername(office.getUsername());
		existing.setPassword(office.getPassword());
		if (office.getStatus() != null && !office.getStatus().equals(existing.getStatus())) {
			existing.setStatus(office.getStatus());
			existing.setStatusDate(LocalDateTime.now());
		}
		return officeRepository.save(existing);
	}

	public List<Office> getAll() {
		return officeRepository.findAll();
	}

	public List<Map<String, Object>> getActiveOffices() {
		List<Map<String, Object>> result = new ArrayList<>();
		for (Office office : officeRepository.findByStatus(1)) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("id", office.getId());
			row.put("facilityId", office.getFacilityId());
			row.put("facilityName", office.getFacilityName());
			result.add(row);
		}
		return result;
	}

	public Office getById(Long id) {
		return officeRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Office not found with id: " + id));
	}

	public Office setInactive(Long id) {
		Office existing = getById(id);
		existing.setStatus(0);
		existing.setStatusDate(LocalDateTime.now());
		return officeRepository.save(existing);
	}

	public Office setActive(Long id) {
		Office existing = getById(id);
		existing.setStatus(1);
		existing.setStatusDate(LocalDateTime.now());
		return officeRepository.save(existing);
	}
}
