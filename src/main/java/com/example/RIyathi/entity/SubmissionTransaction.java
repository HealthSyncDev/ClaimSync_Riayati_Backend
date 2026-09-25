package com.example.RIyathi.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_submissiontransaction")
public class SubmissionTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tableid")
	private Long tableId;

	@Column(name = "id")
	private String id;

	@Column(name = "senderid", nullable = false)
	private String senderId;

	@Column(name = "receiverid", nullable = false)
	private String receiverId;

	@Column(name = "recordcount", nullable = false)
	private Integer recordCount;

	@Column(name = "transactiondate")
	private LocalDateTime transactionDate;

	@Column(name = "creationdate")
	private LocalDateTime creationDate;

	@Column(name = "downloaded", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private Boolean downloaded = false;

	@Column(name = "downloadeddate")
	private LocalDateTime downloadedDate;

	@Column(name = "downloadeddategeneratedstring")
	private String downloadedDateGeneratedString;

	@Column(name = "is_extracted", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private Integer isExtracted = 0;

	@Column(name = "officeid")
	private Long officeId;

	public Long getTableId() {
		return tableId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Integer getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(Integer recordCount) {
		this.recordCount = recordCount;
	}

	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public Boolean getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(Boolean downloaded) {
		this.downloaded = downloaded;
	}

	public LocalDateTime getDownloadedDate() {
		return downloadedDate;
	}

	public void setDownloadedDate(LocalDateTime downloadedDate) {
		this.downloadedDate = downloadedDate;
	}

	public String getDownloadedDateGeneratedString() {
		return downloadedDateGeneratedString;
	}

	public void setDownloadedDateGeneratedString(String downloadedDateGeneratedString) {
		this.downloadedDateGeneratedString = downloadedDateGeneratedString;
	}

	public Integer getIsExtracted() {
		return isExtracted;
	}

	public void setIsExtracted(Integer isExtracted) {
		this.isExtracted = isExtracted;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}
}

