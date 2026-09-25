package com.example.RIyathi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_submissionDiagnosis")
public class SubmissionDiagnosis {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tableid")
	private Long tableId;

	@Column(name = "diagnosistype")
	private String type;

	@Column(name = "code")
	private String code;

	@Column(name = "claimtableid", nullable = false)
	private Long claimTableId;

	public Long getTableId() {
		return tableId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getClaimTableId() {
		return claimTableId;
	}

	public void setClaimTableId(Long claimTableId) {
		this.claimTableId = claimTableId;
	}
}
