package com.example.RIyathi.service;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReportQueryParams {

	private Long officeId;
	private String officeIds;
	private String searchOn;
	private String fromDate;
	private String toDate;
	private Integer year;
	private Integer month;
	private String payerId;
	private String receiverId;
	private String payer;
	private String payerName;
	private String encounterType;
	private String clinician;
	private String clinicianId;
	private String claimNo;
	private String denial;
	private String cpt;
	private String cptCode;
	private String claimStatus;
	private String paymentStatus;
	private String patientId;
	private String memberId;
	private String facilityId;
	private String invoiceNo;
	private String invoiceNoLike;
	private String receiptStatus;
	private String idPayer;
	private String resubmissionType;
	private String facilityName;
	private String receiverName;
	private String clinicianName;
	private String patientType;
	private String encounterTypeDescription;

	public Map<String, String> toFilters() {
		Map<String, String> filters = new LinkedHashMap<>();
		filters.put("payerId", payerId);
		filters.put("receiverId", receiverId);
		filters.put("payer", payer);
		filters.put("payerName", payerName);
		filters.put("encounterType", encounterType);
		filters.put("clinician", clinician);
		filters.put("clinicianId", clinicianId);
		filters.put("claimNo", claimNo);
		filters.put("denial", denial);
		filters.put("cpt", cpt);
		filters.put("cptCode", cptCode);
		filters.put("claimStatus", claimStatus);
		filters.put("paymentStatus", paymentStatus);
		filters.put("patientId", patientId);
		filters.put("memberId", memberId);
		filters.put("facilityId", facilityId);
		filters.put("invoiceNo", invoiceNo);
		filters.put("invoiceNoLike", invoiceNoLike);
		filters.put("receiptStatus", receiptStatus);
		filters.put("idPayer", idPayer);
		filters.put("resubmissionType", resubmissionType);
		filters.put("facilityName", facilityName);
		filters.put("receiverName", receiverName);
		filters.put("clinicianName", clinicianName);
		filters.put("patientType", patientType);
		filters.put("encounterTypeDescription", encounterTypeDescription);
		return filters;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}

	public String getOfficeIds() {
		return officeIds;
	}

	public void setOfficeIds(String officeIds) {
		this.officeIds = officeIds;
	}

	public String getSearchOn() {
		return searchOn;
	}

	public void setSearchOn(String searchOn) {
		this.searchOn = searchOn;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public String getPayerId() {
		return payerId;
	}

	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public String getPayer() {
		return payer;
	}

	public void setPayer(String payer) {
		this.payer = payer;
	}

	public String getPayerName() {
		return payerName;
	}

	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}

	public String getEncounterType() {
		return encounterType;
	}

	public void setEncounterType(String encounterType) {
		this.encounterType = encounterType;
	}

	public String getClinician() {
		return clinician;
	}

	public void setClinician(String clinician) {
		this.clinician = clinician;
	}

	public String getClinicianId() {
		return clinicianId;
	}

	public void setClinicianId(String clinicianId) {
		this.clinicianId = clinicianId;
	}

	public String getClaimNo() {
		return claimNo;
	}

	public void setClaimNo(String claimNo) {
		this.claimNo = claimNo;
	}

	public String getDenial() {
		return denial;
	}

	public void setDenial(String denial) {
		this.denial = denial;
	}

	public String getCpt() {
		return cpt;
	}

	public void setCpt(String cpt) {
		this.cpt = cpt;
	}

	public String getCptCode() {
		return cptCode;
	}

	public void setCptCode(String cptCode) {
		this.cptCode = cptCode;
	}

	public String getClaimStatus() {
		return claimStatus;
	}

	public void setClaimStatus(String claimStatus) {
		this.claimStatus = claimStatus;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getInvoiceNoLike() {
		return invoiceNoLike;
	}

	public void setInvoiceNoLike(String invoiceNoLike) {
		this.invoiceNoLike = invoiceNoLike;
	}

	public String getReceiptStatus() {
		return receiptStatus;
	}

	public void setReceiptStatus(String receiptStatus) {
		this.receiptStatus = receiptStatus;
	}

	public String getIdPayer() {
		return idPayer;
	}

	public void setIdPayer(String idPayer) {
		this.idPayer = idPayer;
	}

	public String getResubmissionType() {
		return resubmissionType;
	}

	public void setResubmissionType(String resubmissionType) {
		this.resubmissionType = resubmissionType;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getClinicianName() {
		return clinicianName;
	}

	public void setClinicianName(String clinicianName) {
		this.clinicianName = clinicianName;
	}

	public String getPatientType() {
		return patientType;
	}

	public void setPatientType(String patientType) {
		this.patientType = patientType;
	}

	public String getEncounterTypeDescription() {
		return encounterTypeDescription;
	}

	public void setEncounterTypeDescription(String encounterTypeDescription) {
		this.encounterTypeDescription = encounterTypeDescription;
	}
}
