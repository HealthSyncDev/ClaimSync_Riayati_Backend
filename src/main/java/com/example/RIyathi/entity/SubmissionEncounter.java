package com.example.RIyathi.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_submissionEncounter")
public class SubmissionEncounter {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tableid")
	private Long tableId;

	@Column(name = "facilityid")
	private String facilityId;

	@Column(name = "encountertype")
	private String type;

	@Column(name = "patientid")
	private String patientId;

	@Column(name = "startdate")
	private LocalDateTime start;

	@Column(name = "enddate")
	private LocalDateTime end;

	@Column(name = "starttype")
	private String startType;

	@Column(name = "endtype")
	private String endType;

	@Column(name = "transfersource")
	private String transferSource;

	@Column(name = "transferdestination")
	private String transferDestination;

	@Column(name = "claimtableid", nullable = false)
	private Long claimTableId;

	public Long getTableId() {
		return tableId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public LocalDateTime getStart() {
		return start;
	}

	public void setStart(LocalDateTime start) {
		this.start = start;
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public void setEnd(LocalDateTime end) {
		this.end = end;
	}

	public String getStartType() {
		return startType;
	}

	public void setStartType(String startType) {
		this.startType = startType;
	}

	public String getEndType() {
		return endType;
	}

	public void setEndType(String endType) {
		this.endType = endType;
	}

	public String getTransferSource() {
		return transferSource;
	}

	public void setTransferSource(String transferSource) {
		this.transferSource = transferSource;
	}

	public String getTransferDestination() {
		return transferDestination;
	}

	public void setTransferDestination(String transferDestination) {
		this.transferDestination = transferDestination;
	}

	public Long getClaimTableId() {
		return claimTableId;
	}

	public void setClaimTableId(Long claimTableId) {
		this.claimTableId = claimTableId;
	}
}
