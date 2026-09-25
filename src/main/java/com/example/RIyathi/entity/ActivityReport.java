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
@Table(name = "tbl_activityReport")
public class ActivityReport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "officeid")
	private Long officeId;

	@Column(name = "FacilityID")
	private String facilityId;

	@Column(name = "Facility_Name")
	private String facilityName;

	@Column(name = "InvoiceNo")
	private String invoiceNo;

	@Column(name = "PatientID")
	private String patientId;

	@Column(name = "TransactionDate")
	private LocalDateTime transactionDate;

	@Column(name = "ActivityStartDate")
	private LocalDateTime activityStartDate;

	@Column(name = "EncounterType")
	private String encounterType;

	@Column(name = "EncounterStartDate")
	private LocalDateTime encounterStartDate;

	@Column(name = "EncounterEndDate")
	private LocalDateTime encounterEndDate;

	@Column(name = "TransferSource")
	private String transferSource;

	@Column(name = "TransferDestination")
	private String transferDestination;

	@Column(name = "ClaimActivityNumber")
	private String claimActivityNumber;

	@Column(name = "CPTCode")
	private String cptCode;

	@Column(name = "CPTCategory")
	private String cptCategory;

	@Column(name = "CPTType")
	private String cptType;

	@Column(name = "Quantity")
	private String quantity;

	@Column(name = "OrderingClinician")
	private String orderingClinician;

	@Column(name = "OrderingClinician_Name")
	private String orderingClinicianName;

	@Column(name = "Clinician")
	private String clinician;

	@Column(name = "Clinician_Name")
	private String clinicianName;

	@Column(name = "ReceiverID")
	private String receiverId;

	@Column(name = "Receiver_Name")
	private String receiverName;

	@Column(name = "PayerID")
	private String payerId;

	@Column(name = "Payer_Name")
	private String payerName;

	@Column(name = "MemberID")
	private String memberId;

	@Column(name = "DispensedID")
	private String dispensedId;

	@Column(name = "ProviderID")
	private String providerId;

	@Column(name = "Weight", precision = 18, scale = 2)
	private BigDecimal weight;

	@Column(name = "DateOfBirth")
	private LocalDateTime dateOfBirth;

	@Column(name = "Gender")
	private String gender;

	@Column(name = "NationalIDNumber")
	private String nationalIdNumber;

	@Column(name = "IDPayer")
	private String idPayer;

	@Column(name = "PaymentReference")
	private String paymentReference;

	@Column(name = "PriorAuthorizationID")
	private String priorAuthorizationId;

	@Column(name = "DispensedActivityID")
	private String dispensedActivityId;

	@Column(name = "NetAmt", precision = 18, scale = 2)
	private BigDecimal netAmt;

	@Column(name = "InitialNetAmt", precision = 18, scale = 2)
	private BigDecimal initialNetAmt;

	@Column(name = "Diagnosis", columnDefinition = "TEXT")
	private String diagnosis;

	@Column(name = "PrimaryDiagnosis")
	private String primaryDiagnosis;

	@Column(name = "LastResubmissionDate")
	private LocalDateTime lastResubmissionDate;

	@Column(name = "FirstRemittanceDate")
	private LocalDateTime firstRemittanceDate;

	@Column(name = "LastRemittanceDate")
	private LocalDateTime lastRemittanceDate;

	@Column(name = "RemittedAmt", precision = 18, scale = 2)
	private BigDecimal remittedAmt;

	@Column(name = "LastRemittedAmount", precision = 18, scale = 2)
	private BigDecimal lastRemittedAmount;

	@Column(name = "InitialRejectedAmt", precision = 18, scale = 2)
	private BigDecimal initialRejectedAmt;

	@Column(name = "RejectedAmt", precision = 18, scale = 2)
	private BigDecimal rejectedAmt;

	@Column(name = "UnprocessedAmt", precision = 18, scale = 2)
	private BigDecimal unprocessedAmt;

	@Column(name = "RejectionPercentage", precision = 18, scale = 2)
	private BigDecimal rejectionPercentage;

	@Column(name = "WriteOffAmt", precision = 18, scale = 2)
	private BigDecimal writeOffAmt;

	@Column(name = "WriteOffStatus")
	private String writeOffStatus;

	@Column(name = "WriteOffComment", columnDefinition = "TEXT")
	private String writeOffComment;

	@Column(name = "LastDenailCode")
	private String lastDenailCode;

	@Column(name = "DenialComment", columnDefinition = "TEXT")
	private String denialComment;

	@Column(name = "DenialCategory")
	private String denialCategory;

	@Column(name = "DenialType")
	private String denialType;

	@Column(name = "InitialDenialCode")
	private String initialDenialCode;

	@Column(name = "InitialDenialComment", columnDefinition = "TEXT")
	private String initialDenialComment;

	@Column(name = "InitialDenialCategory")
	private String initialDenialCategory;

	@Column(name = "InitialDenialType")
	private String initialDenialType;

	@Column(name = "ResubmissionCount")
	private Integer resubmissionCount;

	@Column(name = "RemittanceCount")
	private Integer remittanceCount;

	@Column(name = "RemittanceComment", columnDefinition = "TEXT")
	private String remittanceComment;

	@Column(name = "ResubmissionComment", columnDefinition = "TEXT")
	private String resubmissionComment;

	@Column(name = "ClaimYear")
	private Integer claimYear;

	@Column(name = "ClaimMonth")
	private Integer claimMonth;

	@Column(name = "SubmissionAllTransactionIds", columnDefinition = "TEXT")
	private String submissionAllTransactionIds;

	@Column(name = "LastSubmissionTransactionId")
	private String lastSubmissionTransactionId;

	@Column(name = "LastRemittanceTransactionId")
	private String lastRemittanceTransactionId;

	@Column(name = "SettledAmt", precision = 18, scale = 2)
	private BigDecimal settledAmt;

	@Column(name = "ReceiptStatus")
	private String receiptStatus;

	@Column(name = "InitialDateSettlement")
	private LocalDateTime initialDateSettlement;

	@Column(name = "ClaimStatus")
	private String claimStatus;

	@Column(name = "PaymentStatus")
	private String paymentStatus;

	@Column(name = "Month")
	private String month;

	@Column(name = "Type")
	private String type;

	@Column(name = "GrossAmt", precision = 18, scale = 2)
	private BigDecimal grossAmt;

	@Column(name = "PatientShare", precision = 18, scale = 2)
	private BigDecimal patientShare;

	@Column(name = "ResubmissionType")
	private String resubmissionType;

	@Column(name = "patient_type")
	private String patientType;

	@Column(name = "encounter_type_description")
	private String encounterTypeDescription;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}

	public LocalDateTime getActivityStartDate() {
		return activityStartDate;
	}

	public void setActivityStartDate(LocalDateTime activityStartDate) {
		this.activityStartDate = activityStartDate;
	}

	public String getEncounterType() {
		return encounterType;
	}

	public void setEncounterType(String encounterType) {
		this.encounterType = encounterType;
	}

	public LocalDateTime getEncounterStartDate() {
		return encounterStartDate;
	}

	public void setEncounterStartDate(LocalDateTime encounterStartDate) {
		this.encounterStartDate = encounterStartDate;
	}

	public LocalDateTime getEncounterEndDate() {
		return encounterEndDate;
	}

	public void setEncounterEndDate(LocalDateTime encounterEndDate) {
		this.encounterEndDate = encounterEndDate;
	}

	public String getTransferSource() {
		return transferSource;
	}

	public void setTransferSource(String transferSource) {
		this.transferSource = transferSource;
	}

	public String getTransferDestination() {
		return transferDestination;
	}

	public void setTransferDestination(String transferDestination) {
		this.transferDestination = transferDestination;
	}

	public String getClaimActivityNumber() {
		return claimActivityNumber;
	}

	public void setClaimActivityNumber(String claimActivityNumber) {
		this.claimActivityNumber = claimActivityNumber;
	}

	public String getCptCode() {
		return cptCode;
	}

	public void setCptCode(String cptCode) {
		this.cptCode = cptCode;
	}

	public String getCptCategory() {
		return cptCategory;
	}

	public void setCptCategory(String cptCategory) {
		this.cptCategory = cptCategory;
	}

	public String getCptType() {
		return cptType;
	}

	public void setCptType(String cptType) {
		this.cptType = cptType;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getOrderingClinician() {
		return orderingClinician;
	}

	public void setOrderingClinician(String orderingClinician) {
		this.orderingClinician = orderingClinician;
	}

	public String getOrderingClinicianName() {
		return orderingClinicianName;
	}

	public void setOrderingClinicianName(String orderingClinicianName) {
		this.orderingClinicianName = orderingClinicianName;
	}

	public String getClinician() {
		return clinician;
	}

	public void setClinician(String clinician) {
		this.clinician = clinician;
	}

	public String getClinicianName() {
		return clinicianName;
	}

	public void setClinicianName(String clinicianName) {
		this.clinicianName = clinicianName;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getPayerId() {
		return payerId;
	}

	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}

	public String getPayerName() {
		return payerName;
	}

	public void setPayerName(String payerName) {
		this.payerName = payerName;
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

	public String getNationalIdNumber() {
		return nationalIdNumber;
	}

	public void setNationalIdNumber(String nationalIdNumber) {
		this.nationalIdNumber = nationalIdNumber;
	}

	public String getIdPayer() {
		return idPayer;
	}

	public void setIdPayer(String idPayer) {
		this.idPayer = idPayer;
	}

	public String getPaymentReference() {
		return paymentReference;
	}

	public void setPaymentReference(String paymentReference) {
		this.paymentReference = paymentReference;
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

	public BigDecimal getNetAmt() {
		return netAmt;
	}

	public void setNetAmt(BigDecimal netAmt) {
		this.netAmt = netAmt;
	}

	public BigDecimal getInitialNetAmt() {
		return initialNetAmt;
	}

	public void setInitialNetAmt(BigDecimal initialNetAmt) {
		this.initialNetAmt = initialNetAmt;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public String getPrimaryDiagnosis() {
		return primaryDiagnosis;
	}

	public void setPrimaryDiagnosis(String primaryDiagnosis) {
		this.primaryDiagnosis = primaryDiagnosis;
	}

	public LocalDateTime getLastResubmissionDate() {
		return lastResubmissionDate;
	}

	public void setLastResubmissionDate(LocalDateTime lastResubmissionDate) {
		this.lastResubmissionDate = lastResubmissionDate;
	}

	public LocalDateTime getFirstRemittanceDate() {
		return firstRemittanceDate;
	}

	public void setFirstRemittanceDate(LocalDateTime firstRemittanceDate) {
		this.firstRemittanceDate = firstRemittanceDate;
	}

	public LocalDateTime getLastRemittanceDate() {
		return lastRemittanceDate;
	}

	public void setLastRemittanceDate(LocalDateTime lastRemittanceDate) {
		this.lastRemittanceDate = lastRemittanceDate;
	}

	public BigDecimal getRemittedAmt() {
		return remittedAmt;
	}

	public void setRemittedAmt(BigDecimal remittedAmt) {
		this.remittedAmt = remittedAmt;
	}

	public BigDecimal getLastRemittedAmount() {
		return lastRemittedAmount;
	}

	public void setLastRemittedAmount(BigDecimal lastRemittedAmount) {
		this.lastRemittedAmount = lastRemittedAmount;
	}

	public BigDecimal getInitialRejectedAmt() {
		return initialRejectedAmt;
	}

	public void setInitialRejectedAmt(BigDecimal initialRejectedAmt) {
		this.initialRejectedAmt = initialRejectedAmt;
	}

	public BigDecimal getRejectedAmt() {
		return rejectedAmt;
	}

	public void setRejectedAmt(BigDecimal rejectedAmt) {
		this.rejectedAmt = rejectedAmt;
	}

	public BigDecimal getUnprocessedAmt() {
		return unprocessedAmt;
	}

	public void setUnprocessedAmt(BigDecimal unprocessedAmt) {
		this.unprocessedAmt = unprocessedAmt;
	}

	public BigDecimal getRejectionPercentage() {
		return rejectionPercentage;
	}

	public void setRejectionPercentage(BigDecimal rejectionPercentage) {
		this.rejectionPercentage = rejectionPercentage;
	}

	public BigDecimal getWriteOffAmt() {
		return writeOffAmt;
	}

	public void setWriteOffAmt(BigDecimal writeOffAmt) {
		this.writeOffAmt = writeOffAmt;
	}

	public String getWriteOffStatus() {
		return writeOffStatus;
	}

	public void setWriteOffStatus(String writeOffStatus) {
		this.writeOffStatus = writeOffStatus;
	}

	public String getWriteOffComment() {
		return writeOffComment;
	}

	public void setWriteOffComment(String writeOffComment) {
		this.writeOffComment = writeOffComment;
	}

	public String getLastDenailCode() {
		return lastDenailCode;
	}

	public void setLastDenailCode(String lastDenailCode) {
		this.lastDenailCode = lastDenailCode;
	}

	public String getDenialComment() {
		return denialComment;
	}

	public void setDenialComment(String denialComment) {
		this.denialComment = denialComment;
	}

	public String getDenialCategory() {
		return denialCategory;
	}

	public void setDenialCategory(String denialCategory) {
		this.denialCategory = denialCategory;
	}

	public String getDenialType() {
		return denialType;
	}

	public void setDenialType(String denialType) {
		this.denialType = denialType;
	}

	public String getInitialDenialCode() {
		return initialDenialCode;
	}

	public void setInitialDenialCode(String initialDenialCode) {
		this.initialDenialCode = initialDenialCode;
	}

	public String getInitialDenialComment() {
		return initialDenialComment;
	}

	public void setInitialDenialComment(String initialDenialComment) {
		this.initialDenialComment = initialDenialComment;
	}

	public String getInitialDenialCategory() {
		return initialDenialCategory;
	}

	public void setInitialDenialCategory(String initialDenialCategory) {
		this.initialDenialCategory = initialDenialCategory;
	}

	public String getInitialDenialType() {
		return initialDenialType;
	}

	public void setInitialDenialType(String initialDenialType) {
		this.initialDenialType = initialDenialType;
	}

	public Integer getResubmissionCount() {
		return resubmissionCount;
	}

	public void setResubmissionCount(Integer resubmissionCount) {
		this.resubmissionCount = resubmissionCount;
	}

	public Integer getRemittanceCount() {
		return remittanceCount;
	}

	public void setRemittanceCount(Integer remittanceCount) {
		this.remittanceCount = remittanceCount;
	}

	public String getRemittanceComment() {
		return remittanceComment;
	}

	public void setRemittanceComment(String remittanceComment) {
		this.remittanceComment = remittanceComment;
	}

	public String getResubmissionComment() {
		return resubmissionComment;
	}

	public void setResubmissionComment(String resubmissionComment) {
		this.resubmissionComment = resubmissionComment;
	}

	public Integer getClaimYear() {
		return claimYear;
	}

	public void setClaimYear(Integer claimYear) {
		this.claimYear = claimYear;
	}

	public Integer getClaimMonth() {
		return claimMonth;
	}

	public void setClaimMonth(Integer claimMonth) {
		this.claimMonth = claimMonth;
	}

	public String getSubmissionAllTransactionIds() {
		return submissionAllTransactionIds;
	}

	public void setSubmissionAllTransactionIds(String submissionAllTransactionIds) {
		this.submissionAllTransactionIds = submissionAllTransactionIds;
	}

	public String getLastSubmissionTransactionId() {
		return lastSubmissionTransactionId;
	}

	public void setLastSubmissionTransactionId(String lastSubmissionTransactionId) {
		this.lastSubmissionTransactionId = lastSubmissionTransactionId;
	}

	public String getLastRemittanceTransactionId() {
		return lastRemittanceTransactionId;
	}

	public void setLastRemittanceTransactionId(String lastRemittanceTransactionId) {
		this.lastRemittanceTransactionId = lastRemittanceTransactionId;
	}

	public BigDecimal getSettledAmt() {
		return settledAmt;
	}

	public void setSettledAmt(BigDecimal settledAmt) {
		this.settledAmt = settledAmt;
	}

	public String getReceiptStatus() {
		return receiptStatus;
	}

	public void setReceiptStatus(String receiptStatus) {
		this.receiptStatus = receiptStatus;
	}

	public LocalDateTime getInitialDateSettlement() {
		return initialDateSettlement;
	}

	public void setInitialDateSettlement(LocalDateTime initialDateSettlement) {
		this.initialDateSettlement = initialDateSettlement;
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

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getGrossAmt() {
		return grossAmt;
	}

	public void setGrossAmt(BigDecimal grossAmt) {
		this.grossAmt = grossAmt;
	}

	public BigDecimal getPatientShare() {
		return patientShare;
	}

	public void setPatientShare(BigDecimal patientShare) {
		this.patientShare = patientShare;
	}

	public String getResubmissionType() {
		return resubmissionType;
	}

	public void setResubmissionType(String resubmissionType) {
		this.resubmissionType = resubmissionType;
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
