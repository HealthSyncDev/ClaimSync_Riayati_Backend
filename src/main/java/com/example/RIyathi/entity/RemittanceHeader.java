package com.example.RIyathi.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_remittanceHeader")
public class RemittanceHeader {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tableid")
	private Long tableId;

	@Column(name = "senderid", nullable = false)
	private String senderId;

	@Column(name = "receiverid", nullable = false)
	private String receiverId;

	@Column(name = "transactiondate")
	private LocalDateTime transactionDate;

	@Column(name = "recordcount")
	private Integer recordCount;

	@Column(name = "dispositionflag")
	private String dispositionFlag;

	@Column(name = "payerid")
	private String payerId;

	@Column(name = "transactiontableid", nullable = false)
	private Long transactionTableId;

	@Column(name = "officeid")
	private Long officeId;

	public Long getTableId() {
		return tableId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Integer getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(Integer recordCount) {
		this.recordCount = recordCount;
	}

	public String getDispositionFlag() {
		return dispositionFlag;
	}

	public void setDispositionFlag(String dispositionFlag) {
		this.dispositionFlag = dispositionFlag;
	}

	public String getPayerId() {
		return payerId;
	}

	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}

	public Long getTransactionTableId() {
		return transactionTableId;
	}

	public void setTransactionTableId(Long transactionTableId) {
		this.transactionTableId = transactionTableId;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}
}
