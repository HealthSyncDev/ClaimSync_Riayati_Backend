package com.example.RIyathi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_submissionDxinfo")
public class SubmissionDxinfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tableid")
	private Long tableId;

	@Column(name = "dxtype")
	private String type;

	@Column(name = "code")
	private String code;

	@Column(name = "diagnosistableid", nullable = false)
	private Long diagnosisTableId;

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

	public Long getDiagnosisTableId() {
		return diagnosisTableId;
	}

	public void setDiagnosisTableId(Long diagnosisTableId) {
		this.diagnosisTableId = diagnosisTableId;
	}
}
