package com.example.RIyathi.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_remittanceClaim")
public class RemittanceClaim {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tableid")
	private Long tableId;

	@Column(name = "claimid")
	private String claimId;

	@Column(name = "idpayer")
	private String idPayer;

	@Column(name = "providerid")
	private String providerId;

	@Column(name = "denialcode")
	private String denialCode;

	@Column(name = "paymentreference")
	private String paymentReference;

	@Column(name = "datesettlement")
	private LocalDateTime dateSettlement;

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

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public String getDenialCode() {
		return denialCode;
	}

	public void setDenialCode(String denialCode) {
		this.denialCode = denialCode;
	}

	public String getPaymentReference() {
		return paymentReference;
	}

	public void setPaymentReference(String paymentReference) {
		this.paymentReference = paymentReference;
	}

	public LocalDateTime getDateSettlement() {
		return dateSettlement;
	}

	public void setDateSettlement(LocalDateTime dateSettlement) {
		this.dateSettlement = dateSettlement;
	}

	public Long getHeaderTableId() {
		return headerTableId;
	}

	public void setHeaderTableId(Long headerTableId) {
		this.headerTableId = headerTableId;
	}
}
