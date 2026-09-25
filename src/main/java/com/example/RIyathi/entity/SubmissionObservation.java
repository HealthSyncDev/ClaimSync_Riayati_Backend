package com.example.RIyathi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_submissionObservation")
public class SubmissionObservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tableid")
	private Long tableId;

	@Column(name = "observationtype")
	private String type;

	@Column(name = "code")
	private String code;

	@Column(name = "observationvalue", columnDefinition = "TEXT")
	private String value;

	@Column(name = "valuetype")
	private String valueType;

	@Column(name = "activitytableid", nullable = false)
	private Long activityTableId;

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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public Long getActivityTableId() {
		return activityTableId;
	}

	public void setActivityTableId(Long activityTableId) {
		this.activityTableId = activityTableId;
	}
}
