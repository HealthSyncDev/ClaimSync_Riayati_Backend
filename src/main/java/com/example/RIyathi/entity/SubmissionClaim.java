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
@Table(name = "tbl_submissionClaim")
public class SubmissionClaim {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tableid")
	private Long tableId;

	@Column(name = "claimid")
	private String claimId;

	@Column(name = "idpayer")
	private String idPayer;

	@Column(name = "claimtype")
	private String type;

	@Column(name = "memberid")
	private String memberId;

	@Column(name = "dispensedid")
	private String dispensedId;

	@Column(name = "providerid")
	private String providerId;

	@Column(name = "weight", precision = 18, scale = 2)
	private BigDecimal weight;

	@Column(name = "nationalidnumber")
	private String nationalIdNumber;

	@Column(name = "gross", precision = 18, scale = 2)
	private BigDecimal gross;

	@Column(name = "patientshare", precision = 18, scale = 2)
	private BigDecimal patientShare;

	@Column(name = "net", precision = 18, scale = 2)
	private BigDecimal net;

	@Column(name = "referencenumber")
	private String referenceNumber;

	@Column(name = "dateofbirth")
	private LocalDateTime dateOfBirth;

	@Column(name = "gender")
	private String gender;

	@Column(name = "headertableid", nullable = false)
	private Long headerTableId;

	public Long getTableId() {
		return tableId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public String getClaimId() {
		return claimId;
	}

	public void setClaimId(String claimId) {
		this.claimId = claimId;
	}

	public String getIdPayer() {
		return idPayer;
	}

	public void setIdPayer(String idPayer) {
		this.idPayer = idPayer;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getDispensedId() {
		return dispensedId;
	}

	public void setDispensedId(String dispensedId) {
		this.dispensedId = dispensedId;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public String getNationalIdNumber() {
		return nationalIdNumber;
	}

	public void setNationalIdNumber(String nationalIdNumber) {
		this.nationalIdNumber = nationalIdNumber;
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

	public BigDecimal getNet() {
		return net;
	}

	public void setNet(BigDecimal net) {
		this.net = net;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public LocalDateTime getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDateTime dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Long getHeaderTableId() {
		return headerTableId;
	}

	public void setHeaderTableId(Long headerTableId) {
		this.headerTableId = headerTableId;
	}
}
