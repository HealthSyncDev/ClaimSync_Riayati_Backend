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
@Table(name = "tbl_remittanceActivity")
public class RemittanceActivity {

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

	@Column(name = "code")
	private String code;

	@Column(name = "quantity")
	private String quantity;

	@Column(name = "net", precision = 18, scale = 2)
	private BigDecimal net;

	@Column(name = "listvalue")
	private String list;

	@Column(name = "clinician")
	private String clinician;

	@Column(name = "priorauthorizationid")
	private String priorAuthorizationId;

	@Column(name = "gross", precision = 18, scale = 2)
	private BigDecimal gross;

	@Column(name = "patientshare", precision = 18, scale = 2)
	private BigDecimal patientShare;

	@Column(name = "activitypenalty")
	private String activityPenalty;

	@Column(name = "paymentamount", precision = 18, scale = 2)
	private BigDecimal paymentAmount;

	@Column(name = "denialcode")
	private String denialCode;

	@Column(name = "comments", columnDefinition = "TEXT")
	private String comments;

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

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}

	public String getClinician() {
		return clinician;
	}

	public void setClinician(String clinician) {
		this.clinician = clinician;
	}

	public String getPriorAuthorizationId() {
		return priorAuthorizationId;
	}

	public void setPriorAuthorizationId(String priorAuthorizationId) {
		this.priorAuthorizationId = priorAuthorizationId;
	}

	public BigDecimal getGross() {
		return gross;
	}

	public void setGross(BigDecimal gross) {
		this.gross = gross;
	}

	public BigDecimal getPatientShare() {
		return patientShare;
	}

	public void setPatientShare(BigDecimal patientShare) {
		this.patientShare = patientShare;
	}

	public String getActivityPenalty() {
		return activityPenalty;
	}

	public void setActivityPenalty(String activityPenalty) {
		this.activityPenalty = activityPenalty;
	}

	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getDenialCode() {
		return denialCode;
	}

	public void setDenialCode(String denialCode) {
		this.denialCode = denialCode;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Long getClaimTableId() {
		return claimTableId;
	}

	public void setClaimTableId(Long claimTableId) {
		this.claimTableId = claimTableId;
	}
}
