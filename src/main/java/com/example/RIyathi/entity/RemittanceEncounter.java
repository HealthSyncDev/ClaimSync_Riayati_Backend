package com.example.RIyathi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_remittanceEncounter")
public class RemittanceEncounter {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tableid")
	private Long tableId;

	@Column(name = "facilityid")
	private String facilityId;

	@Column(name = "claimtableid", nullable = false)
	private Long claimTableId;

	public Long getTableId() {
		return tableId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public Long getClaimTableId() {
		return claimTableId;
	}

	public void setClaimTableId(Long claimTableId) {
		this.claimTableId = claimTableId;
	}
}
