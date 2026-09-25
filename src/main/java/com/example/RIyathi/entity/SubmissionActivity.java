package com.example.RIyathi.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_submissionActivity")
public class SubmissionActivity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tableid")
	private Long tableId;

	@Column(name = "activityid")
	private String activityId;

	@Column(name = "startdate")
	private LocalDateTime start;

	@Column(name = "activitytype")
	private String type;

	@Column(name = "location")
	private String location;

	@Column(name = "code")
	private String code;

	@Column(name = "quantity")
	private String quantity;

	@Column(name = "net", precision = 18, scale = 2)
	private BigDecimal net;

	@Column(name = "patientshare", precision = 18, scale = 2)
	private BigDecimal patientShare;

	@Column(name = "clinician")
	private String clinician;

	@Column(name = "duration")
	private String duration;

	@Column(name = "priorauthorizationid")
	private String priorAuthorizationId;

	@Column(name = "dispensedactivityid")
	private String dispensedActivityId;

	@Column(name = "claimtableid", nullable = false)
	private Long claimTableId;

	public Long getTableId() {
		return tableId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public LocalDateTime getStart() {
		return start;
	}

	public void setStart(LocalDateTime start) {
		this.start = start;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getNet() {
		return net;
	}

	public void setNet(BigDecimal net) {
		this.net = net;
	}

	public BigDecimal getPatientShare() {
		return patientShare;
	}

	public void setPatientShare(BigDecimal patientShare) {
		this.patientShare = patientShare;
	}

	public String getClinician() {
		return clinician;
	}

	public void setClinician(String clinician) {
		this.clinician = clinician;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getPriorAuthorizationId() {
		return priorAuthorizationId;
	}

	public void setPriorAuthorizationId(String priorAuthorizationId) {
		this.priorAuthorizationId = priorAuthorizationId;
	}

	public String getDispensedActivityId() {
		return dispensedActivityId;
	}

	public void setDispensedActivityId(String dispensedActivityId) {
		this.dispensedActivityId = dispensedActivityId;
	}

	public Long getClaimTableId() {
		return claimTableId;
	}

	public void setClaimTableId(Long claimTableId) {
		this.claimTableId = claimTableId;
	}
}
