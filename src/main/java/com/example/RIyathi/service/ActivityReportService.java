package com.example.RIyathi.service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import com.example.RIyathi.entity.ActivityReport;
import com.example.RIyathi.entity.Office;
import com.example.RIyathi.entity.RemittanceHeader;
import com.example.RIyathi.entity.SubmissionHeader;
import com.example.RIyathi.repository.OfficeRepository;
import com.example.RIyathi.repository.RemittanceHeaderRepository;
import com.example.RIyathi.repository.SubmissionHeaderRepository;

@Service
public class ActivityReportService {

	private final OfficeRepository officeRepository;
	private final SubmissionHeaderRepository submissionHeaderRepository;
	private final RemittanceHeaderRepository remittanceHeaderRepository;
	private final ActivityReportGenerator activityReportGenerator;

	@PersistenceContext
	private EntityManager entityManager;

	public ActivityReportService(
			OfficeRepository officeRepository,
			SubmissionHeaderRepository submissionHeaderRepository,
			RemittanceHeaderRepository remittanceHeaderRepository,
			ActivityReportGenerator activityReportGenerator) {
		this.officeRepository = officeRepository;
		this.submissionHeaderRepository = submissionHeaderRepository;
		this.remittanceHeaderRepository = remittanceHeaderRepository;
		this.activityReportGenerator = activityReportGenerator;
	}

	public Object list(ReportQueryParams query, int page, int size) {
		int pageNumber = page < 1 ? 1 : page;
		int pageSize = size < 1 || size > 500 ? 500 : size;
		Loaded loaded = load(query, pageNumber, pageSize, true);

		List<Map<String, Object>> data = new ArrayList<>(loaded.rows());
		data.add(loaded.total());

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("page", pageNumber);
		body.put("size", pageSize);
		body.put("totalElements", loaded.totalElements());
		body.put("totalPages", loaded.totalPages());
		body.put("officeId", query.getOfficeId());
		body.put("officeIds", loaded.officeIds());
		body.put("data", data);
		return body;
	}

	public List<Map<String, Object>> exportRows(ReportQueryParams query) {
		Loaded loaded = load(query, 1, Integer.MAX_VALUE, false);
		List<Map<String, Object>> rows = new ArrayList<>(loaded.rows());
		rows.add(alignActivityTotal(loaded.total()));
		return rows;
	}

	private Loaded load(ReportQueryParams query, int pageNumber, int pageSize, boolean paginate) {
		ActivityReportQueryBuilder.Filter filter = ActivityReportQueryBuilder.build(
				query.getOfficeId(), query.getOfficeIds(), query.getSearchOn(), query.getFromDate(), query.getToDate(),
				query.getYear(), query.getMonth(), query.toFilters(), false);
		String from = " FROM tbl_activityReport ar" + filter.whereSql();

		Number count = (Number) bind(entityManager.createNativeQuery("SELECT COUNT(*)" + from), filter.params())
				.getSingleResult();
		long totalElements = count == null ? 0L : count.longValue();
		int totalPages = pageSize == Integer.MAX_VALUE || totalElements == 0
				? (totalElements == 0 ? 0 : 1)
				: (int) Math.ceil(totalElements / (double) pageSize);

		Query pageQuery = bind(entityManager.createNativeQuery(
				"SELECT ar.* " + from + " ORDER BY ar.id",
				ActivityReport.class), filter.params());
		if (paginate) {
			pageQuery.setFirstResult((pageNumber - 1) * pageSize);
			pageQuery.setMaxResults(pageSize);
		}

		@SuppressWarnings("unchecked")
		List<ActivityReport> entities = pageQuery.getResultList();
		List<Map<String, Object>> rows = new ArrayList<>();
		for (ActivityReport report : entities) {
			rows.add(toColumnMap(report));
		}
		return new Loaded(rows, toTotalRow(from, filter.params()), totalElements, totalPages, filter.officeIds());
	}

	public Object generate(Long officeId) {
		List<Long> officeIds = resolveOfficeIds(officeId);
		int inserted = 0;
		for (Long id : officeIds) {
			inserted += activityReportGenerator.generateOffice(id);
		}

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("success", true);
		result.put("officeId", officeId);
		result.put("officesProcessed", officeIds.size());
		result.put("insertedCount", inserted);
		return result;
	}

	private List<Long> resolveOfficeIds(Long officeId) {
		if (officeId != null) {
			officeRepository.findById(officeId)
					.orElseThrow(() -> new ResponseStatusException(
							HttpStatus.NOT_FOUND, "Office not found with id: " + officeId));
			return List.of(officeId);
		}

		Set<Long> ids = new LinkedHashSet<>();
		for (Office office : officeRepository.findAll()) {
			if (office.getId() != null) {
				ids.add(office.getId());
			}
		}
		for (SubmissionHeader header : submissionHeaderRepository.findAll()) {
			if (header.getOfficeId() != null) {
				ids.add(header.getOfficeId());
			}
		}
		for (RemittanceHeader header : remittanceHeaderRepository.findAll()) {
			if (header.getOfficeId() != null) {
				ids.add(header.getOfficeId());
			}
		}
		if (ids.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No offices found to generate activity report");
		}
		return new ArrayList<>(ids);
	}

	private Map<String, Object> toColumnMap(ActivityReport report) {
		Map<String, Object> row = new LinkedHashMap<>();
		for (Field field : ActivityReport.class.getDeclaredFields()) {
			Column column = field.getAnnotation(Column.class);
			if (column == null) {
				continue;
			}
			field.setAccessible(true);
			try {
				row.put(column.name(), field.get(report));
			} catch (IllegalAccessException ex) {
				row.put(column.name(), null);
			}
		}
		return row;
	}

	private Map<String, Object> toTotalRow(String from, Map<String, Object> params) {
		String sql = """
				SELECT
				  SUM(ar.GrossAmt),
				  SUM(ar.NetAmt),
				  SUM(ar.InitialNetAmt),
				  SUM(ar.PatientShare),
				  SUM(ar.RemittedAmt),
				  SUM(ar.RejectedAmt),
				  SUM(ar.InitialRejectedAmt),
				  SUM(ar.WriteOffAmt),
				  SUM(ar.SettledAmt)
				""" + from;
		Object result = bind(entityManager.createNativeQuery(sql), params).getSingleResult();
		Object[] values;
		if (result instanceof Object[] array) {
			values = array.length == 1 && array[0] instanceof Object[] nested ? nested : array;
		} else {
			values = new Object[] { result };
		}
		Map<String, Object> total = new LinkedHashMap<>();
		total.put("isTotalRow", true);
		total.put("label", "TOTAL");
		total.put("grossAmt", amount(values, 0));
		total.put("netAmt", amount(values, 1));
		total.put("initialNetAmt", amount(values, 2));
		total.put("claimPatientShareAmt", amount(values, 3));
		total.put("paymentAmt", amount(values, 4));
		total.put("rejectedAmt", amount(values, 5));
		total.put("initialRejectedAmt", amount(values, 6));
		total.put("writeOffAmt", amount(values, 7));
		total.put("settledAmt", amount(values, 8));
		return total;
	}

	private Query bind(Query query, Map<String, Object> params) {
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return query;
	}

	private Object amount(Object[] values, int index) {
		if (values == null || index >= values.length || values[index] == null) {
			return BigDecimal.ZERO;
		}
		return values[index];
	}

	private Map<String, Object> alignActivityTotal(Map<String, Object> total) {
		Map<String, Object> row = toColumnMap(new ActivityReport());
		row.put("InvoiceNo", "TOTAL");
		row.put("GrossAmt", total.get("grossAmt"));
		row.put("NetAmt", total.get("netAmt"));
		row.put("InitialNetAmt", total.get("initialNetAmt"));
		row.put("PatientShare", total.get("claimPatientShareAmt"));
		row.put("RemittedAmt", total.get("paymentAmt"));
		row.put("RejectedAmt", total.get("rejectedAmt"));
		row.put("InitialRejectedAmt", total.get("initialRejectedAmt"));
		row.put("WriteOffAmt", total.get("writeOffAmt"));
		row.put("SettledAmt", total.get("settledAmt"));
		return row;
	}

	private record Loaded(
			List<Map<String, Object>> rows,
			Map<String, Object> total,
			long totalElements,
			int totalPages,
			List<Long> officeIds) {
	}
}
