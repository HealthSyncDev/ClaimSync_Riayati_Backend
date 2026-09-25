package com.example.RIyathi.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.RIyathi.entity.ActivityReport;
import com.example.RIyathi.entity.Office;
import com.example.RIyathi.entity.RemittanceActivity;
import com.example.RIyathi.entity.RemittanceClaim;
import com.example.RIyathi.entity.RemittanceHeader;
import com.example.RIyathi.entity.RemittanceTransaction;
import com.example.RIyathi.entity.SubmissionActivity;
import com.example.RIyathi.entity.SubmissionClaim;
import com.example.RIyathi.entity.SubmissionDiagnosis;
import com.example.RIyathi.entity.SubmissionEncounter;
import com.example.RIyathi.entity.SubmissionHeader;
import com.example.RIyathi.entity.SubmissionResubmission;
import com.example.RIyathi.entity.SubmissionTransaction;
import com.example.RIyathi.repository.ActivityReportRepository;
import com.example.RIyathi.repository.OfficeRepository;
import com.example.RIyathi.repository.RemittanceActivityRepository;
import com.example.RIyathi.repository.RemittanceClaimRepository;
import com.example.RIyathi.repository.RemittanceHeaderRepository;
import com.example.RIyathi.repository.RemittanceTransactionRepository;
import com.example.RIyathi.repository.SubmissionActivityRepository;
import com.example.RIyathi.repository.SubmissionClaimRepository;
import com.example.RIyathi.repository.SubmissionDiagnosisRepository;
import com.example.RIyathi.repository.SubmissionEncounterRepository;
import com.example.RIyathi.repository.SubmissionHeaderRepository;
import com.example.RIyathi.repository.SubmissionResubmissionRepository;
import com.example.RIyathi.repository.SubmissionTransactionRepository;

@Component
public class ActivityReportGenerator {

	private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");
	private static final Pattern TRAILING_DIGITS = Pattern.compile("(\\d+)$");
	private static final Set<String> NEVER_COLLAPSE_PAY_REF = Set.of(
			"0", "000000", "DENIED", "N/A", "NIL", "NOT APPLICABLE");

	private final TransactionTemplate transactionTemplate;
	private final ActivityReportRepository activityReportRepository;
	private final OfficeRepository officeRepository;
	private final SubmissionHeaderRepository submissionHeaderRepository;
	private final SubmissionClaimRepository submissionClaimRepository;
	private final SubmissionActivityRepository submissionActivityRepository;
	private final SubmissionEncounterRepository submissionEncounterRepository;
	private final SubmissionDiagnosisRepository submissionDiagnosisRepository;
	private final SubmissionResubmissionRepository submissionResubmissionRepository;
	private final SubmissionTransactionRepository submissionTransactionRepository;
	private final RemittanceHeaderRepository remittanceHeaderRepository;
	private final RemittanceClaimRepository remittanceClaimRepository;
	private final RemittanceActivityRepository remittanceActivityRepository;
	private final RemittanceTransactionRepository remittanceTransactionRepository;

	public ActivityReportGenerator(
			PlatformTransactionManager transactionManager,
			ActivityReportRepository activityReportRepository,
			OfficeRepository officeRepository,
			SubmissionHeaderRepository submissionHeaderRepository,
			SubmissionClaimRepository submissionClaimRepository,
			SubmissionActivityRepository submissionActivityRepository,
			SubmissionEncounterRepository submissionEncounterRepository,
			SubmissionDiagnosisRepository submissionDiagnosisRepository,
			SubmissionResubmissionRepository submissionResubmissionRepository,
			SubmissionTransactionRepository submissionTransactionRepository,
			RemittanceHeaderRepository remittanceHeaderRepository,
			RemittanceClaimRepository remittanceClaimRepository,
			RemittanceActivityRepository remittanceActivityRepository,
			RemittanceTransactionRepository remittanceTransactionRepository) {
		this.transactionTemplate = new TransactionTemplate(transactionManager);
		this.activityReportRepository = activityReportRepository;
		this.officeRepository = officeRepository;
		this.submissionHeaderRepository = submissionHeaderRepository;
		this.submissionClaimRepository = submissionClaimRepository;
		this.submissionActivityRepository = submissionActivityRepository;
		this.submissionEncounterRepository = submissionEncounterRepository;
		this.submissionDiagnosisRepository = submissionDiagnosisRepository;
		this.submissionResubmissionRepository = submissionResubmissionRepository;
		this.submissionTransactionRepository = submissionTransactionRepository;
		this.remittanceHeaderRepository = remittanceHeaderRepository;
		this.remittanceClaimRepository = remittanceClaimRepository;
		this.remittanceActivityRepository = remittanceActivityRepository;
		this.remittanceTransactionRepository = remittanceTransactionRepository;
	}

	public int generateOffice(Long officeId) {
		Integer inserted = transactionTemplate.execute(status -> rebuildOffice(officeId));
		return inserted == null ? 0 : inserted;
	}

	private int rebuildOffice(Long officeId) {
		activityReportRepository.deleteByOfficeId(officeId);
		Office office = officeRepository.findById(officeId).orElse(null);
		OfficeData data = loadOfficeData(officeId);
		List<ActivityReport> rows = new ArrayList<>();

		Set<String> invoices = new HashSet<>();
		invoices.addAll(data.submissionsByInvoice.keySet());
		invoices.addAll(data.remitsByInvoice.keySet());

		for (String invoiceKey : invoices) {
			rows.addAll(buildInvoiceRows(officeId, office, invoiceKey, data));
		}

		dropDuplicateInvoiceActivity(rows);
		insertByMonth(rows);
		return rows.size();
	}

	private List<ActivityReport> buildInvoiceRows(Long officeId, Office office, String invoiceKey, OfficeData data) {
		List<SubmissionFile> submissions = new ArrayList<>(data.submissionsByInvoice.getOrDefault(invoiceKey, List.of()));
		submissions.sort(this::compareSubmissions);

		for (int i = 0; i < submissions.size(); i++) {
			SubmissionFile current = submissions.get(i);
			current.nextDate = i + 1 < submissions.size() ? submissions.get(i + 1).date : null;
		}

		LocalDateTime firstSubDate = submissions.stream().map(s -> s.date).filter(Objects::nonNull).min(LocalDateTime::compareTo).orElse(null);
		LocalDateTime lastResubDate = submissions.size() > 1
				? submissions.stream().map(s -> s.date).filter(Objects::nonNull).max(LocalDateTime::compareTo).orElse(null)
				: null;
		int resubmissionCount = Math.max(0, submissions.size() - 1);

		List<SubmissionFile> firstSubs = submissions.stream().filter(s -> s.kind == ResubKind.FIRST).toList();
		SubmissionFile winnerFirst = null;
		Set<Long> superseded = new HashSet<>();
		Set<String> winnerActivityIds = Set.of();
		if (firstSubs.size() >= 2) {
			winnerFirst = firstSubs.stream().max(this::compareSubmissions).orElse(null);
			for (SubmissionFile first : firstSubs) {
				if (winnerFirst != null && !Objects.equals(first.claim.getTableId(), winnerFirst.claim.getTableId())) {
					superseded.add(first.claim.getTableId());
				}
			}
			if (winnerFirst != null) {
				winnerActivityIds = activityIds(winnerFirst);
			}
		}

		boolean hasCorrection = submissions.stream().anyMatch(s -> s.kind == ResubKind.CORRECTION);
		boolean hasReconciliation = submissions.stream().anyMatch(s -> s.kind == ResubKind.RECONCILIATION);
		Set<String> correctionActivityIds = new HashSet<>();
		Set<String> reconActivityIds = new HashSet<>();
		for (SubmissionFile sub : submissions) {
			if (sKind(sub, ResubKind.CORRECTION)) {
				correctionActivityIds.addAll(activityIds(sub));
			}
			if (sKind(sub, ResubKind.RECONCILIATION)) {
				reconActivityIds.addAll(activityIds(sub));
			}
		}

		Map<String, Integer> activitySubmissionCount = new HashMap<>();
		for (SubmissionFile sub : submissions) {
			if (superseded.contains(sub.claim.getTableId())) {
				continue;
			}
			for (String activityId : activityIds(sub)) {
				activitySubmissionCount.merge(norm(activityId), 1, Integer::sum);
			}
		}

		List<RemitLine> remits = new ArrayList<>(data.remitsByInvoice.getOrDefault(invoiceKey, List.of()));
		applyValidReceiverFilter(remits, winnerFirst, winnerActivityIds);
		applyPaymentRefDedupe(remits, winnerActivityIds);
		assignRemitsToSubmissions(remits, submissions, superseded, activitySubmissionCount);

		LocalDateTime firstRemitDate = remits.stream().map(r -> r.date).filter(Objects::nonNull).min(LocalDateTime::compareTo).orElse(null);
		boolean remittanceSourced = submissions.isEmpty()
				|| (firstRemitDate != null && (firstSubDate == null || firstRemitDate.isBefore(firstSubDate)));

		List<Candidate> candidates = remittanceSourced
				? remittanceCandidates(officeId, office, invoiceKey, submissions, remits, data)
				: submissionCandidates(officeId, office, invoiceKey, submissions, superseded, hasCorrection, correctionActivityIds);

		if (candidates.isEmpty()) {
			return List.of();
		}

		assignChains(candidates, hasCorrection || hasReconciliation);
		applyChainRemits(candidates, remits, submissions);

		for (Candidate candidate : candidates) {
			candidate.keepKey = hasReconciliation && !reconActivityIds.contains(norm(candidate.activityId))
					? norm(candidate.activityId)
					: candidate.chainKey;
			fillReport(candidate, office, invoiceKey, firstSubDate, lastResubDate, resubmissionCount, submissions, remittanceSourced);
		}

		return keepLatest(candidates);
	}

	private List<Candidate> submissionCandidates(
			Long officeId,
			Office office,
			String invoiceKey,
			List<SubmissionFile> submissions,
			Set<Long> superseded,
			boolean hasCorrection,
			Set<String> correctionActivityIds) {
		List<Candidate> candidates = new ArrayList<>();
		for (SubmissionFile sub : submissions) {
			if (superseded.contains(sub.claim.getTableId())) {
				continue;
			}
			for (SubmissionActivity activity : sub.activities) {
				if (!hasText(activity.getActivityId())) {
					continue;
				}
				if (hasCorrection && !correctionActivityIds.contains(norm(activity.getActivityId()))) {
					continue;
				}
				Candidate candidate = new Candidate();
				candidate.officeId = officeId;
				candidate.submission = sub;
				candidate.activity = activity;
				candidate.activityId = activity.getActivityId();
				candidate.cptCode = activity.getCode();
				candidate.invoiceNo = firstNonBlank(sub.claim.getClaimId(), claimInvoice(sub.claim));
				candidates.add(candidate);
			}
		}
		return candidates;
	}

	private List<Candidate> remittanceCandidates(
			Long officeId,
			Office office,
			String invoiceKey,
			List<SubmissionFile> submissions,
			List<RemitLine> remits,
			OfficeData data) {
		RemitLine earliest = remits.stream()
				.min(Comparator.comparing((RemitLine r) -> r.date != null ? r.date : LocalDateTime.MAX)
						.thenComparing(r -> r.claim.getTableId() == null ? Long.MAX_VALUE : r.claim.getTableId()))
				.orElse(null);
		if (earliest == null) {
			return List.of();
		}
		SubmissionFile borrow = submissions.stream().min(this::compareSubmissions).orElse(null);
		Map<Long, List<RemitLine>> byClaim = remits.stream().collect(Collectors.groupingBy(r -> r.claim.getTableId()));
		List<RemitLine> earliestActivities = byClaim.getOrDefault(earliest.claim.getTableId(), List.of());

		List<Candidate> candidates = new ArrayList<>();
		Set<String> seen = new HashSet<>();
		for (RemitLine line : earliestActivities) {
			String activityId = firstNonBlank(line.activity.getActivityId());
			if (!hasText(activityId) || !seen.add(norm(activityId))) {
				continue;
			}
			Candidate candidate = new Candidate();
			candidate.officeId = officeId;
			candidate.submission = borrow;
			candidate.activity = findSubmissionActivity(submissions, activityId);
			candidate.remittanceActivity = line.activity;
			candidate.activityId = activityId;
			candidate.cptCode = line.activity.getCode();
			candidate.invoiceNo = firstNonBlank(line.claim.getClaimId());
			candidates.add(candidate);
		}
		return candidates;
	}

	private SubmissionActivity findSubmissionActivity(List<SubmissionFile> submissions, String activityId) {
		for (SubmissionFile sub : submissions) {
			for (SubmissionActivity activity : sub.activities) {
				if (norm(activity.getActivityId()).equals(norm(activityId))) {
					return activity;
				}
			}
		}
		return null;
	}

	private void applyValidReceiverFilter(List<RemitLine> remits, SubmissionFile winnerFirst, Set<String> winnerActivityIds) {
		if (winnerFirst == null) {
			return;
		}
		String winnerReceiver = norm(winnerFirst.header.getReceiverId());
		remits.removeIf(line -> {
			boolean activityOnWinner = winnerActivityIds.contains(norm(line.activity.getActivityId()));
			boolean senderMatches = winnerReceiver.equals(norm(line.header.getSenderId()));
			return !(activityOnWinner && senderMatches);
		});
	}

	private void applyPaymentRefDedupe(List<RemitLine> remits, Set<String> winnerActivityIds) {
		Map<Long, Integer> matchScore = new HashMap<>();
		Map<Long, List<RemitLine>> byClaim = remits.stream().collect(Collectors.groupingBy(r -> r.claim.getTableId()));
		for (Map.Entry<Long, List<RemitLine>> entry : byClaim.entrySet()) {
			int score = 0;
			for (RemitLine line : entry.getValue()) {
				if (winnerActivityIds.contains(norm(line.activity.getActivityId()))) {
					score++;
				}
			}
			matchScore.put(entry.getKey(), score);
		}

		Map<String, List<RemitLine>> partitions = new LinkedHashMap<>();
		for (RemitLine line : remits) {
			String payRef = trim(line.claim.getPaymentReference());
			String amountKey = line.activity.getPaymentAmount() == null
					? "0"
					: line.activity.getPaymentAmount().stripTrailingZeros().toPlainString();
			String key = norm(line.activity.getActivityId()) + "|" + payRef + "|" + amountKey;
			partitions.computeIfAbsent(key, k -> new ArrayList<>()).add(line);
		}

		for (List<RemitLine> group : partitions.values()) {
			boolean neverCollapse = isNeverCollapsePayRef(group.get(0).claim.getPaymentReference());
			if (neverCollapse) {
				for (RemitLine line : group) {
					line.keepForPayment = true;
				}
				continue;
			}
			group.sort(Comparator
					.comparing((RemitLine r) -> matchScore.getOrDefault(r.claim.getTableId(), 0)).reversed()
					.thenComparing(r -> r.date != null ? r.date : LocalDateTime.MAX)
					.thenComparing(r -> r.claim.getTableId() == null ? Long.MAX_VALUE : r.claim.getTableId()));
			for (int i = 0; i < group.size(); i++) {
				group.get(i).keepForPayment = i == 0;
			}
		}
	}

	private void assignRemitsToSubmissions(
			List<RemitLine> remits,
			List<SubmissionFile> submissions,
			Set<Long> superseded,
			Map<String, Integer> activitySubmissionCount) {
		for (RemitLine line : remits) {
			String activityKey = norm(line.activity.getActivityId());
			boolean msa = activitySubmissionCount.getOrDefault(activityKey, 0) > 1;
			List<SubmissionFile> owners = submissions.stream()
					.filter(s -> !superseded.contains(s.claim.getTableId()))
					.filter(s -> hasActivity(s, activityKey))
					.sorted(this::compareSubmissions)
					.toList();
			SubmissionFile assigned = null;
			for (SubmissionFile sub : owners) {
				if (line.date == null || sub.date == null) {
					continue;
				}
				if (line.date.isBefore(sub.date)) {
					continue;
				}
				boolean inWindow = sub.nextDate == null || line.date.isBefore(sub.nextDate);
				if (inWindow || !msa) {
					assigned = sub;
					if (inWindow) {
						break;
					}
				}
			}
			if (assigned == null && !msa) {
				assigned = owners.stream()
						.filter(s -> line.date != null && s.date != null && !line.date.isBefore(s.date))
						.reduce((a, b) -> b)
						.orElse(null);
			}
			line.submissionTableId = assigned == null ? null : assigned.claim.getTableId();
		}
	}

	private void assignChains(List<Candidate> candidates, boolean chainEnabled) {
		if (!chainEnabled) {
			for (Candidate candidate : candidates) {
				candidate.chainKey = norm(candidate.activityId);
			}
			return;
		}

		Map<String, Set<Long>> prefixSubs = new HashMap<>();
		Map<String, Set<Long>> codeSubs = new HashMap<>();
		for (Candidate candidate : candidates) {
			Long subId = candidate.submission == null ? -1L : candidate.submission.claim.getTableId();
			prefixSubs.computeIfAbsent(activityPrefix(candidate.activityId), k -> new HashSet<>()).add(subId);
			if (hasText(candidate.cptCode)) {
				codeSubs.computeIfAbsent(norm(candidate.cptCode), k -> new HashSet<>()).add(subId);
			}
		}
		for (Candidate candidate : candidates) {
			String prefix = activityPrefix(candidate.activityId);
			if (prefixSubs.getOrDefault(prefix, Set.of()).size() >= 2) {
				candidate.chainKey = "P:" + prefix;
			} else if (hasText(candidate.cptCode) && codeSubs.getOrDefault(norm(candidate.cptCode), Set.of()).size() >= 2) {
				candidate.chainKey = "C:" + norm(candidate.cptCode);
			} else {
				candidate.chainKey = norm(candidate.activityId);
			}
		}
	}

	private void applyChainRemits(List<Candidate> candidates, List<RemitLine> remits, List<SubmissionFile> submissions) {
		Map<String, List<Candidate>> byChain = candidates.stream().collect(Collectors.groupingBy(c -> c.chainKey));
		Set<Long> windowSubmissionIds = remits.stream()
				.map(r -> r.submissionTableId)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());

		for (List<Candidate> chain : byChain.values()) {
			Set<String> activityKeys = chain.stream().map(c -> norm(c.activityId)).collect(Collectors.toSet());
			List<RemitLine> chainRemits = remits.stream()
					.filter(r -> activityKeys.contains(norm(r.activity.getActivityId())))
					.sorted(Comparator.comparing((RemitLine r) -> r.date != null ? r.date : LocalDateTime.MAX))
					.toList();

			BigDecimal remitted = BigDecimal.ZERO;
			boolean anyPay = false;
			for (RemitLine line : chainRemits) {
				if (line.keepForPayment && line.activity.getPaymentAmount() != null) {
					remitted = remitted.add(line.activity.getPaymentAmount());
					anyPay = true;
				}
			}

			RemitLine first = chainRemits.isEmpty() ? null : chainRemits.get(0);
			RemitLine last = chainRemits.isEmpty() ? null : chainRemits.get(chainRemits.size() - 1);
			RemitLine lastKept = chainRemits.stream().filter(r -> r.keepForPayment).reduce((a, b) -> b).orElse(last);

			BigDecimal initialNet = chain.stream()
					.map(c -> c.activity != null ? c.activity.getNet() : (c.remittanceActivity != null ? c.remittanceActivity.getNet() : null))
					.filter(Objects::nonNull)
					.findFirst()
					.orElse(null);

			for (Candidate candidate : chain) {
				candidate.chainRemits = chainRemits;
				candidate.remittedAmt = anyPay ? remitted : null;
				candidate.firstRemit = first;
				candidate.lastRemit = last;
				candidate.lastKeptRemit = lastKept;
				candidate.initialNet = initialNet;
				boolean inWindow = candidate.submission != null
						&& windowSubmissionIds.contains(candidate.submission.claim.getTableId());
				boolean hasLine = chainRemits.stream()
						.anyMatch(r -> norm(r.activity.getActivityId()).equals(norm(candidate.activityId))
								&& (candidate.submission == null
										|| Objects.equals(r.submissionTableId, candidate.submission.claim.getTableId())));
				if (!hasLine) {
					hasLine = chainRemits.stream().anyMatch(r -> norm(r.activity.getActivityId()).equals(norm(candidate.activityId)));
				}
				candidate.inSubmissionWindow = inWindow;
				candidate.hasRemitLine = hasLine;
			}
		}
	}

	private void fillReport(
			Candidate candidate,
			Office office,
			String invoiceKey,
			LocalDateTime firstSubDate,
			LocalDateTime lastResubDate,
			int resubmissionCount,
			List<SubmissionFile> submissions,
			boolean remittanceSourced) {
		ActivityReport report = new ActivityReport();
		candidate.report = report;
		report.setOfficeId(candidate.officeId);
		report.setInvoiceNo(candidate.invoiceNo);

		SubmissionFile sub = candidate.submission;
		SubmissionActivity activity = candidate.activity;
		RemittanceActivity remitActivity = candidate.remittanceActivity;

		if (sub != null && sub.encounter != null) {
			SubmissionEncounter encounter = sub.encounter;
			report.setFacilityId(encounter.getFacilityId());
			report.setPatientId(encounter.getPatientId());
			report.setEncounterType(encounter.getType());
			report.setEncounterStartDate(encounter.getStart());
			report.setEncounterEndDate(encounter.getEnd());
			report.setTransferSource(encounter.getTransferSource());
			report.setTransferDestination(encounter.getTransferDestination());
		}
		if (office != null) {
			if (!hasText(report.getFacilityId())) {
				report.setFacilityId(office.getFacilityId());
			}
			report.setFacilityName(office.getFacilityName());
		}

		if (sub != null) {
			report.setTransactionDate(sub.date);
			report.setReceiverId(sub.header.getReceiverId());
			report.setPayerId(sub.header.getPayerId());
			report.setMemberId(sub.claim.getMemberId());
			report.setDispensedId(sub.claim.getDispensedId());
			report.setProviderId(sub.claim.getProviderId());
			report.setWeight(sub.claim.getWeight());
			report.setDateOfBirth(sub.claim.getDateOfBirth());
			report.setGender(sub.claim.getGender());
			report.setNationalIdNumber(sub.claim.getNationalIdNumber());
			report.setIdPayer(sub.claim.getIdPayer());
			report.setType(sub.claim.getType());
			report.setPatientType(sub.claim.getType());
			report.setDiagnosis(joinDiagnoses(sub.diagnoses));
			report.setPrimaryDiagnosis(principalDiagnosis(sub.diagnoses));
			if (sub.resubmission != null) {
				report.setResubmissionComment(sub.resubmission.getComment());
				report.setResubmissionType(sub.resubmission.getType());
			}
		}

		if (activity != null) {
			report.setActivityStartDate(activity.getStart());
			report.setClaimActivityNumber(activity.getActivityId());
			report.setCptCode(activity.getCode());
			report.setCptType(activity.getType());
			report.setQuantity(activity.getQuantity());
			report.setClinician(activity.getClinician());
			report.setPriorAuthorizationId(activity.getPriorAuthorizationId());
			report.setDispensedActivityId(activity.getDispensedActivityId());
			report.setNetAmt(activity.getNet());
			report.setPatientShare(activity.getPatientShare());
		} else if (remitActivity != null) {
			report.setActivityStartDate(remitActivity.getStart());
			report.setClaimActivityNumber(remitActivity.getActivityId());
			report.setCptCode(remitActivity.getCode());
			report.setCptType(remitActivity.getType());
			report.setQuantity(remitActivity.getQuantity());
			report.setClinician(remitActivity.getClinician());
			report.setPriorAuthorizationId(remitActivity.getPriorAuthorizationId());
			report.setNetAmt(remitActivity.getNet());
			report.setGrossAmt(remitActivity.getGross());
			if (report.getPatientShare() == null) {
				report.setPatientShare(remitActivity.getPatientShare());
			}
			if (candidate.firstRemit != null) {
				report.setProviderId(firstNonBlank(report.getProviderId(), candidate.firstRemit.claim.getProviderId()));
				report.setIdPayer(firstNonBlank(report.getIdPayer(), candidate.firstRemit.claim.getIdPayer()));
			}
		}

		report.setInitialNetAmt(candidate.initialNet);
		report.setLastResubmissionDate(lastResubDate);
		report.setResubmissionCount(resubmissionCount);

		List<RemitLine> chainRemits = candidate.chainRemits == null ? List.of() : candidate.chainRemits;
		report.setRemittanceCount(chainRemits.size());
		report.setRemittedAmt(candidate.remittedAmt);
		if (candidate.firstRemit != null) {
			report.setFirstRemittanceDate(candidate.firstRemit.date);
			report.setInitialDenialCode(firstNonBlank(candidate.firstRemit.activity.getDenialCode(), candidate.firstRemit.claim.getDenialCode()));
			report.setInitialDenialComment(candidate.firstRemit.activity.getComments());
			report.setInitialDateSettlement(candidate.firstRemit.claim.getDateSettlement());
		}
		if (candidate.lastRemit != null) {
			report.setLastRemittanceDate(candidate.lastRemit.date);
			report.setLastDenailCode(firstNonBlank(candidate.lastRemit.activity.getDenialCode(), candidate.lastRemit.claim.getDenialCode()));
			report.setDenialComment(candidate.lastRemit.activity.getComments());
			report.setLastRemittanceTransactionId(candidate.lastRemit.transactionId);
			report.setPaymentReference(candidate.lastRemit.claim.getPaymentReference());
		}
		if (candidate.lastKeptRemit != null) {
			report.setLastRemittedAmount(candidate.lastKeptRemit.activity.getPaymentAmount());
		}

		BigDecimal net = report.getNetAmt();
		if (candidate.hasRemitLine) {
			BigDecimal remitted = zeroIfNull(candidate.remittedAmt);
			BigDecimal rejected = net == null ? null : net.subtract(remitted);
			if (rejected != null && rejected.signum() < 0) {
				rejected = BigDecimal.ZERO;
			}
			report.setRejectedAmt(rejected);
			report.setUnprocessedAmt(BigDecimal.ZERO);
			report.setClaimStatus("Remitted");
			report.setPaymentStatus(remitted.signum() > 0 ? "Paid" : "Rejected");
		} else if (candidate.inSubmissionWindow) {
			report.setRejectedAmt(net);
			report.setUnprocessedAmt(BigDecimal.ZERO);
			report.setClaimStatus("Rejected");
			report.setPaymentStatus("Rejected");
		} else {
			report.setUnprocessedAmt(net);
			report.setRejectedAmt(BigDecimal.ZERO);
			report.setClaimStatus(remittanceSourced ? "Remitted" : "Submitted");
			report.setPaymentStatus("Unprocessed");
		}
		if (candidate.firstRemit != null && report.getInitialRejectedAmt() == null) {
			BigDecimal firstPay = candidate.firstRemit.activity.getPaymentAmount();
			if (net != null && firstPay != null) {
				BigDecimal initialRejected = net.subtract(firstPay);
				report.setInitialRejectedAmt(initialRejected.signum() < 0 ? BigDecimal.ZERO : initialRejected);
			}
		}
		if (net != null && net.signum() != 0 && report.getRejectedAmt() != null) {
			report.setRejectionPercentage(report.getRejectedAmt()
					.multiply(BigDecimal.valueOf(100))
					.divide(net, 2, RoundingMode.HALF_UP));
		}

		report.setRemittanceComment(chainRemits.stream()
				.map(r -> r.activity.getComments())
				.filter(this::hasText)
				.distinct()
				.collect(Collectors.joining(" | ")));
		if (!hasText(report.getRemittanceComment())) {
			report.setRemittanceComment(null);
		}

		LocalDateTime yearSource = remittanceSourced
				? (candidate.firstRemit != null ? candidate.firstRemit.date : firstSubDate)
				: firstSubDate;
		if (yearSource != null) {
			report.setClaimYear(yearSource.getYear());
			report.setClaimMonth(yearSource.getMonthValue());
			report.setMonth(yearSource.format(MONTH_FORMAT));
		}

		List<String> submissionTxIds = submissions.stream()
				.map(s -> s.transactionId)
				.filter(this::hasText)
				.distinct()
				.toList();
		if (!submissionTxIds.isEmpty()) {
			report.setSubmissionAllTransactionIds(String.join(",", submissionTxIds));
			report.setLastSubmissionTransactionId(submissionTxIds.get(submissionTxIds.size() - 1));
		} else if (sub != null) {
			report.setLastSubmissionTransactionId(sub.transactionId);
			report.setSubmissionAllTransactionIds(sub.transactionId);
		}
	}

	private List<ActivityReport> keepLatest(List<Candidate> candidates) {
		Map<String, Candidate> winners = new LinkedHashMap<>();
		for (Candidate candidate : candidates) {
			String key = norm(candidate.invoiceNo) + "|" + candidate.keepKey;
			Candidate existing = winners.get(key);
			if (existing == null || better(candidate, existing)) {
				winners.put(key, candidate);
			}
		}
		return winners.values().stream().map(c -> c.report).filter(Objects::nonNull).toList();
	}

	private boolean better(Candidate a, Candidate b) {
		long aNum = numericActivity(a.activityId);
		long bNum = numericActivity(b.activityId);
		if (aNum != bNum) {
			return aNum > bNum;
		}
		LocalDateTime aDate = a.submission == null ? LocalDateTime.MIN : nvl(a.submission.date);
		LocalDateTime bDate = b.submission == null ? LocalDateTime.MIN : nvl(b.submission.date);
		int cmp = aDate.compareTo(bDate);
		if (cmp != 0) {
			return cmp > 0;
		}
		long aId = a.submission == null || a.submission.claim.getTableId() == null ? 0L : a.submission.claim.getTableId();
		long bId = b.submission == null || b.submission.claim.getTableId() == null ? 0L : b.submission.claim.getTableId();
		return aId > bId;
	}

	private void dropDuplicateInvoiceActivity(List<ActivityReport> rows) {
		Map<String, ActivityReport> winners = new LinkedHashMap<>();
		List<ActivityReport> snapshot = new ArrayList<>(rows);
		for (ActivityReport row : snapshot) {
			String key = row.getOfficeId() + "|" + norm(row.getInvoiceNo()) + "|" + norm(row.getClaimActivityNumber());
			ActivityReport existing = winners.get(key);
			if (existing == null || numericActivity(row.getClaimActivityNumber()) > numericActivity(existing.getClaimActivityNumber())) {
				winners.put(key, row);
			} else if (numericActivity(row.getClaimActivityNumber()) == numericActivity(existing.getClaimActivityNumber())
					&& nvl(row.getTransactionDate()).isAfter(nvl(existing.getTransactionDate()))) {
				winners.put(key, row);
			}
		}
		rows.clear();
		rows.addAll(winners.values());
	}

	private void insertByMonth(List<ActivityReport> rows) {
		Map<String, List<ActivityReport>> byMonth = new LinkedHashMap<>();
		for (ActivityReport row : rows) {
			String month = hasText(row.getMonth()) ? row.getMonth() : "unknown";
			byMonth.computeIfAbsent(month, k -> new ArrayList<>()).add(row);
		}
		for (List<ActivityReport> chunk : byMonth.values()) {
			activityReportRepository.saveAll(chunk);
		}
	}

	private OfficeData loadOfficeData(Long officeId) {
		OfficeData data = new OfficeData();

		List<SubmissionHeader> subHeaders = submissionHeaderRepository.findByOfficeId(officeId);
		Map<Long, SubmissionHeader> subHeaderById = indexHeaders(subHeaders);
		List<Long> subHeaderIds = subHeaders.stream().map(SubmissionHeader::getTableId).toList();
		List<SubmissionClaim> subClaims = inList(subHeaderIds, submissionClaimRepository::findByHeaderTableIdIn);
		List<Long> subClaimIds = subClaims.stream().map(SubmissionClaim::getTableId).toList();
		Map<Long, List<SubmissionActivity>> activitiesByClaim = groupActivities(
				inList(subClaimIds, submissionActivityRepository::findByClaimTableIdIn));
		Map<Long, SubmissionEncounter> encounterByClaim = inList(subClaimIds, submissionEncounterRepository::findByClaimTableIdIn)
				.stream()
				.collect(Collectors.toMap(SubmissionEncounter::getClaimTableId, e -> e, (a, b) -> a));
		Map<Long, List<SubmissionDiagnosis>> dxByClaim = inList(subClaimIds, submissionDiagnosisRepository::findByClaimTableIdIn)
				.stream()
				.collect(Collectors.groupingBy(SubmissionDiagnosis::getClaimTableId));
		Map<Long, SubmissionResubmission> resubByClaim = inList(subClaimIds, submissionResubmissionRepository::findByClaimTableIdIn)
				.stream()
				.collect(Collectors.toMap(SubmissionResubmission::getClaimTableId, r -> r, (a, b) -> a));
		Set<Long> subTxIds = subHeaders.stream().map(SubmissionHeader::getTransactionTableId).filter(Objects::nonNull).collect(Collectors.toSet());
		Map<Long, SubmissionTransaction> subTxById = subTxIds.isEmpty()
				? Map.of()
				: submissionTransactionRepository.findAllById(subTxIds).stream()
						.collect(Collectors.toMap(SubmissionTransaction::getTableId, t -> t));

		for (SubmissionClaim claim : subClaims) {
			if (!hasText(claim.getClaimId())) {
				continue;
			}
			SubmissionHeader header = subHeaderById.get(claim.getHeaderTableId());
			if (header == null) {
				continue;
			}
			SubmissionFile file = new SubmissionFile();
			file.claim = claim;
			file.header = header;
			file.encounter = encounterByClaim.get(claim.getTableId());
			file.diagnoses = dxByClaim.getOrDefault(claim.getTableId(), List.of());
			file.resubmission = resubByClaim.get(claim.getTableId());
			file.activities = activitiesByClaim.getOrDefault(claim.getTableId(), List.of());
			file.transaction = subTxById.get(header.getTransactionTableId());
			file.transactionId = file.transaction == null ? null : file.transaction.getId();
			file.date = header.getTransactionDate() != null
					? header.getTransactionDate()
					: (file.transaction == null ? null : file.transaction.getTransactionDate());
			file.kind = classifyResub(file.resubmission);
			data.submissionsByInvoice.computeIfAbsent(norm(claim.getClaimId()), k -> new ArrayList<>()).add(file);
		}

		List<RemittanceHeader> remHeaders = remittanceHeaderRepository.findByOfficeId(officeId);
		Map<Long, RemittanceHeader> remHeaderById = remHeaders.stream()
				.collect(Collectors.toMap(RemittanceHeader::getTableId, h -> h, (a, b) -> a));
		List<Long> remHeaderIds = remHeaders.stream().map(RemittanceHeader::getTableId).toList();
		List<RemittanceClaim> remClaims = inList(remHeaderIds, remittanceClaimRepository::findByHeaderTableIdIn);
		List<Long> remClaimIds = remClaims.stream().map(RemittanceClaim::getTableId).toList();
		Map<Long, List<RemittanceActivity>> remActs = inList(remClaimIds, remittanceActivityRepository::findByClaimTableIdIn)
				.stream()
				.collect(Collectors.groupingBy(RemittanceActivity::getClaimTableId));
		Set<Long> remTxIds = remHeaders.stream().map(RemittanceHeader::getTransactionTableId).filter(Objects::nonNull).collect(Collectors.toSet());
		Map<Long, RemittanceTransaction> remTxById = remTxIds.isEmpty()
				? Map.of()
				: remittanceTransactionRepository.findAllById(remTxIds).stream()
						.collect(Collectors.toMap(RemittanceTransaction::getTableId, t -> t));

		for (RemittanceClaim claim : remClaims) {
			if (!hasText(claim.getClaimId())) {
				continue;
			}
			RemittanceHeader header = remHeaderById.get(claim.getHeaderTableId());
			if (header == null) {
				continue;
			}
			RemittanceTransaction tx = remTxById.get(header.getTransactionTableId());
			String txId = tx == null ? null : tx.getId();
			LocalDateTime date = header.getTransactionDate() != null
					? header.getTransactionDate()
					: (tx == null ? claim.getDateSettlement() : tx.getTransactionDate());
			for (RemittanceActivity activity : remActs.getOrDefault(claim.getTableId(), List.of())) {
				RemitLine line = new RemitLine();
				line.claim = claim;
				line.header = header;
				line.activity = activity;
				line.date = date != null ? date : activity.getStart();
				line.transactionId = txId;
				line.keepForPayment = true;
				data.remitsByInvoice.computeIfAbsent(norm(claim.getClaimId()), k -> new ArrayList<>()).add(line);
			}
		}
		return data;
	}

	private Map<Long, SubmissionHeader> indexHeaders(List<SubmissionHeader> headers) {
		return headers.stream().collect(Collectors.toMap(SubmissionHeader::getTableId, h -> h, (a, b) -> a));
	}

	private Map<Long, List<SubmissionActivity>> groupActivities(List<SubmissionActivity> activities) {
		return activities.stream().collect(Collectors.groupingBy(SubmissionActivity::getClaimTableId));
	}

	private <T> List<T> inList(Collection<Long> ids, java.util.function.Function<Collection<Long>, List<T>> loader) {
		if (ids == null || ids.isEmpty()) {
			return List.of();
		}
		return loader.apply(ids);
	}

	private int compareSubmissions(SubmissionFile a, SubmissionFile b) {
		int cmp = nvl(a.date).compareTo(nvl(b.date));
		if (cmp != 0) {
			return cmp;
		}
		long aId = a.claim.getTableId() == null ? 0L : a.claim.getTableId();
		long bId = b.claim.getTableId() == null ? 0L : b.claim.getTableId();
		return Long.compare(aId, bId);
	}

	private Set<String> activityIds(SubmissionFile file) {
		return file.activities.stream()
				.map(SubmissionActivity::getActivityId)
				.filter(this::hasText)
				.map(this::norm)
				.collect(Collectors.toSet());
	}

	private boolean hasActivity(SubmissionFile file, String activityKey) {
		return file.activities.stream().anyMatch(a -> norm(a.getActivityId()).equals(activityKey));
	}

	private boolean sKind(SubmissionFile file, ResubKind kind) {
		return file.kind == kind;
	}

	private ResubKind classifyResub(SubmissionResubmission resubmission) {
		if (resubmission == null || !hasText(resubmission.getType())) {
			return ResubKind.FIRST;
		}
		String type = resubmission.getType().trim().toLowerCase();
		if (type.contains("correct")) {
			return ResubKind.CORRECTION;
		}
		if (type.contains("reconcil")) {
			return ResubKind.RECONCILIATION;
		}
		return ResubKind.OTHER;
	}

	private boolean isNeverCollapsePayRef(String paymentReference) {
		String value = trim(paymentReference).toUpperCase();
		if (value.isBlank()) {
			return true;
		}
		return NEVER_COLLAPSE_PAY_REF.contains(value);
	}

	private String activityPrefix(String activityId) {
		String value = activityId == null ? "" : activityId.trim();
		if (value.length() <= 1) {
			return norm(value);
		}
		return norm(value.substring(0, value.length() - 1));
	}

	private long numericActivity(String activityId) {
		if (!hasText(activityId)) {
			return 0L;
		}
		Matcher matcher = TRAILING_DIGITS.matcher(activityId.trim());
		if (matcher.find()) {
			try {
				return Long.parseLong(matcher.group(1));
			} catch (NumberFormatException ignored) {
				return 0L;
			}
		}
		return 0L;
	}

	private String joinDiagnoses(List<SubmissionDiagnosis> diagnoses) {
		if (diagnoses == null || diagnoses.isEmpty()) {
			return null;
		}
		String joined = diagnoses.stream()
				.map(d -> (nvl(d.getType()) + " " + nvl(d.getCode())).trim())
				.filter(s -> !s.isBlank())
				.collect(Collectors.joining(", "));
		return joined.isBlank() ? null : joined;
	}

	private String principalDiagnosis(List<SubmissionDiagnosis> diagnoses) {
		if (diagnoses == null) {
			return null;
		}
		return diagnoses.stream()
				.filter(d -> {
					String type = d.getType() == null ? "" : d.getType().trim();
					return type.equalsIgnoreCase("Principal")
							|| type.equalsIgnoreCase("Primary")
							|| type.equalsIgnoreCase("P");
				})
				.map(SubmissionDiagnosis::getCode)
				.filter(this::hasText)
				.findFirst()
				.orElse(null);
	}

	private String claimInvoice(SubmissionClaim claim) {
		return claim.getClaimId();
	}

	private String firstNonBlank(String... values) {
		for (String value : values) {
			if (hasText(value)) {
				return value;
			}
		}
		return null;
	}

	private boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

	private String trim(String value) {
		return value == null ? "" : value.trim();
	}

	private String norm(String value) {
		return trim(value).toLowerCase();
	}

	private String nvl(String value) {
		return value == null ? "" : value;
	}

	private LocalDateTime nvl(LocalDateTime value) {
		return value == null ? LocalDateTime.MIN : value;
	}

	private BigDecimal zeroIfNull(BigDecimal value) {
		return value == null ? BigDecimal.ZERO : value;
	}

	private enum ResubKind {
		FIRST, CORRECTION, RECONCILIATION, OTHER
	}

	private static final class OfficeData {
		private final Map<String, List<SubmissionFile>> submissionsByInvoice = new HashMap<>();
		private final Map<String, List<RemitLine>> remitsByInvoice = new HashMap<>();
	}

	private static final class SubmissionFile {
		private SubmissionClaim claim;
		private SubmissionHeader header;
		private SubmissionEncounter encounter;
		private List<SubmissionDiagnosis> diagnoses = List.of();
		private SubmissionResubmission resubmission;
		private List<SubmissionActivity> activities = List.of();
		private SubmissionTransaction transaction;
		private String transactionId;
		private LocalDateTime date;
		private LocalDateTime nextDate;
		private ResubKind kind;
	}

	private static final class RemitLine {
		private RemittanceClaim claim;
		private RemittanceHeader header;
		private RemittanceActivity activity;
		private LocalDateTime date;
		private String transactionId;
		private boolean keepForPayment;
		private Long submissionTableId;
	}

	private static final class Candidate {
		private Long officeId;
		private SubmissionFile submission;
		private SubmissionActivity activity;
		private RemittanceActivity remittanceActivity;
		private String invoiceNo;
		private String activityId;
		private String cptCode;
		private String chainKey;
		private String keepKey;
		private List<RemitLine> chainRemits;
		private BigDecimal remittedAmt;
		private BigDecimal initialNet;
		private RemitLine firstRemit;
		private RemitLine lastRemit;
		private RemitLine lastKeptRemit;
		private boolean inSubmissionWindow;
		private boolean hasRemitLine;
		private ActivityReport report;
	}
}
