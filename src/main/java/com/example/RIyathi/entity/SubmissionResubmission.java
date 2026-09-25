package com.example.RIyathi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_submissionResubmission")
public class SubmissionResubmission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tableid")
	private Long tableId;

	@Column(name = "attachment", columnDefinition = "TEXT")
	private String attachment;

	@Column(name = "resubmissioncomment", columnDefinition = "TEXT")
	private String comment;

	@Column(name = "resubmissiontype")
	private String type;

	@Column(name = "claimtableid", nullable = false)
	private Long claimTableId;

	public Long getTableId() {
		return tableId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getClaimTableId() {
		return claimTableId;
	}

	public void setClaimTableId(Long claimTableId) {
		this.claimTableId = claimTableId;
	}
}
